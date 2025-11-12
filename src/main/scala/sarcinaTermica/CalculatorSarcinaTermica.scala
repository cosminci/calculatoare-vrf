package sarcinaTermica

import sarcinaTermica.Orientare.*
import squants.energy.*
import squants.energy.PowerConversions.*
import squants.mass.*
import squants.mass.DensityConversions.given
import squants.radio.*
import squants.radio.IrradianceConversions.*
import squants.space.*
import squants.thermal.*
import squants.thermal.TemperatureConversions.given

import scala.collection.immutable.ListMap

/** Calculator de sarcină termică pentru răcire conform normativelor I5-2022 și SR 6648-2 Acest modul conține logica
  * generică pentru calculul si afișarea raportului pentru sarcinii termice de răcire.
  */
object RadiatieSolara:

  // Alias scurt pentru sintaxa implicită - face tabelele mai compacte și lizibile
  extension (value: Double) private inline def W_m2: Irradiance = value.wattsPerSquareMeter

  private val RadiatieDifuza: Map[Int, Irradiance] = Map(
    6  -> 53.W_m2,
    7  -> 80.W_m2,
    8  -> 103.W_m2,
    9  -> 123.W_m2,
    10 -> 136.W_m2,
    11 -> 146.W_m2,
    12 -> 147.W_m2,
    13 -> 146.W_m2,
    14 -> 136.W_m2,
    15 -> 123.W_m2,
    16 -> 103.W_m2,
    17 -> 80.W_m2,
    18 -> 53.W_m2
  )

  // format: off
  private val RadiatieDirecta: Map[Int, Map[Orientare, Irradiance]] =
    Map(
      6  -> Map(N -> 53.W_m2, NE -> 333.W_m2, E -> 383.W_m2, SE -> 188.W_m2, S -> 0.W_m2,   SV -> 0.W_m2,   V -> 0.W_m2,   NV -> 0.W_m2),
      7  -> Map(N -> 3.W_m2,  NE -> 402.W_m2, E -> 568.W_m2, SE -> 370.W_m2, S -> 0.W_m2,   SV -> 0.W_m2,   V -> 0.W_m2,   NV -> 0.W_m2),
      8  -> Map(N -> 0.W_m2,  NE -> 301.W_m2, E -> 575.W_m2, SE -> 468.W_m2, S -> 41.W_m2,  SV -> 0.W_m2,   V -> 0.W_m2,   NV -> 0.W_m2),
      9  -> Map(N -> 0.W_m2,  NE -> 130.W_m2, E -> 498.W_m2, SE -> 514.W_m2, S -> 159.W_m2, SV -> 0.W_m2,   V -> 0.W_m2,   NV -> 0.W_m2),
      10 -> Map(N -> 0.W_m2,  NE -> 4.W_m2,   E -> 338.W_m2, SE -> 485.W_m2, S -> 316.W_m2, SV -> 0.W_m2,   V -> 0.W_m2,   NV -> 0.W_m2),
      11 -> Map(N -> 0.W_m2,  NE -> 0.W_m2,   E -> 144.W_m2, SE -> 393.W_m2, S -> 354.W_m2, SV -> 58.W_m2,  V -> 0.W_m2,   NV -> 0.W_m2),
      12 -> Map(N -> 0.W_m2,  NE -> 0.W_m2,   E -> 0.W_m2,   SE -> 241.W_m2, S -> 394.W_m2, SV -> 241.W_m2, V -> 0.W_m2,   NV -> 0.W_m2),
      13 -> Map(N -> 0.W_m2,  NE -> 0.W_m2,   E -> 0.W_m2,   SE -> 58.W_m2,  S -> 354.W_m2, SV -> 393.W_m2, V -> 144.W_m2, NV -> 0.W_m2),
      14 -> Map(N -> 0.W_m2,  NE -> 0.W_m2,   E -> 0.W_m2,   SE -> 0.W_m2,   S -> 316.W_m2, SV -> 485.W_m2, V -> 338.W_m2, NV -> 8.W_m2),
      15 -> Map(N -> 0.W_m2,  NE -> 0.W_m2,   E -> 0.W_m2,   SE -> 0.W_m2,   S -> 159.W_m2, SV -> 514.W_m2, V -> 498.W_m2, NV -> 130.W_m2),
      16 -> Map(N -> 0.W_m2,  NE -> 0.W_m2,   E -> 0.W_m2,   SE -> 0.W_m2,   S -> 41.W_m2,  SV -> 468.W_m2, V -> 575.W_m2, NV -> 301.W_m2),
      17 -> Map(N -> 0.W_m2,  NE -> 0.W_m2,   E -> 0.W_m2,   SE -> 0.W_m2,   S -> 0.W_m2,   SV -> 370.W_m2, V -> 568.W_m2, NV -> 402.W_m2),
      18 -> Map(N -> 53.W_m2, NE -> 0.W_m2,   E -> 0.W_m2,   SE -> 0.W_m2,   S -> 0.W_m2,   SV -> 188.W_m2, V -> 383.W_m2, NV -> 333.W_m2)
    )
  // format: on

  def radiatieLaOra(orientare: Orientare, ora: Int): Irradiance =
    radiatieDirectaLaOra(orientare, ora) + radiatieDifuzaLaOra(ora)

  def radiatieDirectaLaOra(orientare: Orientare, ora: Int): Irradiance =
    RadiatieDirecta.get(ora).flatMap(_.get(orientare)).getOrElse(0.W_m2)

  def radiatieDifuzaLaOra(ora: Int): Irradiance =
    RadiatieDifuza.getOrElse(ora, 0.W_m2)

  private def radiatieTotalaLaOra(ferestre: List[Fereastra], ora: Int): Double =
    def radiatie(f: Fereastra) = 
      val radiatie = radiatieLaOra(f.orientare, ora).toWattsPerSquareMeter
      radiatie * f.suprafata.toSquareMeters * f.factorSolar * f.factorUmbra
      
    ferestre.map(radiatie).sum

  def oraRadiatieMaxima(ferestre: List[Fereastra]): Int =
    (6 to 18).maxBy(ora => radiatieTotalaLaOra(ferestre, ora))

trait CalculatorCastigTermic[A]:
  def calculeaza(input: A, parametriClimatici: ParametriClimatici): CastigTermic

object CalculatorPeretiExterni extends CalculatorCastigTermic[Seq[PereteExterior]]:

  def calculeaza(pereti: Seq[PereteExterior], parametri: ParametriClimatici): CastigTermic =
    CastigTermic(
      sursa = "Câștiguri termice prin pereți externi",
      formula = "Q = Σ(U × A × ΔT)",
      valoare = pereti.map(calculeaza(_, parametri)).map(_.valoare).sum,
      componente = pereti.map(calculeaza(_, parametri))
    )

  private def calculeaza(perete: PereteExterior, parametri: ParametriClimatici) =
    val deltaT      = parametri.deltaTemperatura
    val putereWatts = perete.coeficientU * perete.suprafata.toSquareMeters * deltaT.toCelsiusDegrees
    val putere      = putereWatts.watts
    Componenta(
      nume = s"Perete ${perete.orientare}",
      valoare = putere,
      parametri = ListMap(
        "dimensiuni"         -> s"${perete.latime} × ${perete.inaltime}",
        "A (suprafață)"      -> s"${perete.suprafata}",
        "U (coef. transm.)"  -> s"${perete.coeficientU} W/(m²·K)",
        "ΔT (dif. temp.)"    -> s"$deltaT"
      )
    )

object CalculatorFerestre extends CalculatorCastigTermic[Seq[Fereastra]]:

  def calculeaza(ferestre: Seq[Fereastra], parametri: ParametriClimatici): CastigTermic =
    val deltaT     = parametri.deltaTemperatura
    val oraCritica = RadiatieSolara.oraRadiatieMaxima(ferestre.toList)
    val componente = ferestre.map(calculeaza(oraCritica, _, deltaT))

    CastigTermic(
      sursa = "Câștiguri termice prin ferestre",
      formula = s"Q = Q_transmisie + Q_radiatie(ora $oraCritica:00) = Σ(U × A × ΔT) + Σ(A × I × g × F_umbra)",
      valoare = componente.map(_.valoare).sum,
      componente = componente
    )

  private def calculeaza(oraCritica: Int, fereastra: Fereastra, deltaT: Temperature) =
    val qTransWatts    = fereastra.coeficientU * fereastra.suprafata.toSquareMeters * deltaT.toCelsiusDegrees
    val qTrans         = qTransWatts.watts
    val radiatieTotala = RadiatieSolara.radiatieLaOra(fereastra.orientare, oraCritica)
    val qRadWatts =
      fereastra.suprafata.toSquareMeters * radiatieTotala.toWattsPerSquareMeter * fereastra.factorSolar * fereastra.factorUmbra
    val qRad = qRadWatts.watts

    Componenta(
      nume = s"Fereastră ${fereastra.orientare}",
      valoare = qTrans + qRad,
      parametri = ListMap(
        "dimensiuni"              -> s"${fereastra.latime} × ${fereastra.inaltime}",
        "A (suprafață)"           -> s"${fereastra.suprafata}",
        "U (coef. transm.)"       -> s"${fereastra.coeficientU} W/(m²·K)",
        "g (factor solar)"        -> s"${fereastra.factorSolar}",
        "F_umbra (factor umbră)"  -> s"${fereastra.factorUmbra}",
        "ora critică"             -> s"$oraCritica:00",
        "I (radiație totală)"     -> s"$radiatieTotala",
        "I_D (radiație directă)"  -> s"${RadiatieSolara.radiatieDirectaLaOra(fereastra.orientare, oraCritica)}",
        "I_d (radiație difuză)"   -> s"${RadiatieSolara.radiatieDifuzaLaOra(oraCritica)}",
        "ΔT (dif. temp.)"         -> s"$deltaT",
        "Q_trans (transmisie)"    -> s"$qTrans",
        "Q_rad (radiație)"        -> s"$qRad"
      )
    )

object CalculatorPlafon extends CalculatorCastigTermic[Plafon]:

  private val DeltaTRadiatie = 12.celsius // Supraîncălzirea plafonului prin radiație solară

  def calculeaza(plafon: Plafon, parametri: ParametriClimatici): CastigTermic =
    val deltaT        = parametri.deltaTemperatura
    val deltaTEfectiv = if plafon.expus then deltaT + DeltaTRadiatie else deltaT
    val qWatts        = plafon.coeficientU * plafon.suprafata.toSquareMeters * deltaTEfectiv.toCelsiusDegrees
    val q             = qWatts.watts

    val componenta = Componenta(
      nume = if plafon.expus then "Plafon expus" else "Plafon neexpus",
      valoare = q,
      parametri = ListMap(
        "A (suprafață)"           -> s"${plafon.suprafata}",
        "U (coef. transm.)"       -> s"${plafon.coeficientU} W/(m²·K)",
        "ΔT_bază (dif. temp.)"    -> s"$deltaT",
        "ΔT_rad (radiație sol.)"  -> s"${if plafon.expus then DeltaTRadiatie else 0.celsius}",
        "ΔT_ef (efectiv)"         -> s"$deltaTEfectiv"
      )
    )

    CastigTermic(
      sursa = "Câștiguri termice prin plafon",
      formula = "Q = U × A × ΔT_echivalent",
      valoare = q,
      componente = Seq(componenta)
    )

object CalculatorPersoane extends CalculatorCastigTermic[Ocupanti]:

  private val CastigAdultSensibil = 75.watts
  private val CastigAdultLatent   = 50.watts
  private val CastigCopilSensibil = 50.watts
  private val CastigCopilLatent   = 30.watts

  def calculeaza(ocupanti: Ocupanti, parametri: ParametriClimatici): CastigTermic =
    val qAdultiSensibil = CastigAdultSensibil * ocupanti.numarAdulti
    val qAdultiLatent   = CastigAdultLatent * ocupanti.numarAdulti

    val componenta = Componenta(
      nume = "Adulți",
      valoare = qAdultiSensibil + qAdultiLatent,
      parametri = ListMap(
        "n (număr persoane)"     -> s"${ocupanti.numarAdulti}",
        "Q_sens (căldură sens.)" -> s"$qAdultiSensibil",
        "Q_lat (căldură lat.)"   -> s"$qAdultiLatent"
      )
    )

    CastigTermic(
      sursa = "Câștiguri termice de la persoane",
      formula = "Q = n_adulti × q_adult + n_copii × q_copil",
      valoare = componenta.valoare,
      componente = Seq(componenta)
    )

object CalculatorEchipamente extends CalculatorCastigTermic[Echipamente]:

  def calculeaza(echipamente: Echipamente, parametri: ParametriClimatici): CastigTermic =
    val putereEfectiva = echipamente.putereElectrocasnice * echipamente.factorUtilizare

    val componenta = Componenta(
      nume = "Echipamente electrocasnice",
      valoare = putereEfectiva,
      parametri = ListMap(
        "P (putere instalată)"   -> s"${echipamente.putereElectrocasnice}",
        "f (factor utilizare)"   -> s"${echipamente.factorUtilizare}"
      )
    )

    CastigTermic(
      sursa = "Câștiguri termice de la echipamente electrocasnice",
      formula = "Q = P_electrocasnice × f_utilizare",
      valoare = putereEfectiva,
      componente = Seq(componenta)
    )

object CalculatorVentilatie extends CalculatorCastigTermic[Volume]:
  private val DensitateAer          = 1.2.kilogramsPerCubicMeter
  private val CalduraSpecificaAer   = 1005.0 // J/(kg·K)
  private val NSchimburiAerStandard = 0.3    // h⁻¹

  def calculeaza(volum: Volume, parametri: ParametriClimatici): CastigTermic =
    val deltaT     = parametri.deltaTemperatura
    val nSchimburi = NSchimburiAerStandard

    val debitVolumicOrar    = volum * nSchimburi
    val debitVolumicSecunda = debitVolumicOrar / 3600.0
    val debitMasicKgPerS    = DensitateAer.toKilogramsPerCubicMeter * debitVolumicSecunda.toCubicMeters
    val putereWatts         = debitMasicKgPerS * CalduraSpecificaAer * deltaT.toCelsiusDegrees
    val putere              = putereWatts.watts

    val componenta = Componenta(
      nume = "Ventilație/infiltrații",
      valoare = putere,
      parametri = ListMap(
        "V (volum)"              -> s"$volum",
        "n (schimburi aer)"      -> s"$nSchimburi h⁻¹",
        "ρ (densitate aer)"      -> s"${DensitateAer.toKilogramsPerCubicMeter} kg/m³",
        "c_p (căldură spec.)"    -> s"$CalduraSpecificaAer J/(kg·K)",
        "ṁ (debit masic)"        -> s"${formatNumber(debitMasicKgPerS, 5)} kg/s",
        "ΔT"                     -> s"$deltaT"
      )
    )

    CastigTermic(
      sursa = "Câștiguri termice prin ventilație/infiltrații",
      formula = "Q = ṁ × c_p × ΔT = (ρ × V × n / 3600) × c_p × ΔT",
      valoare = putere,
      componente = Seq(componenta)
    )

case class RezultatCalcul(castiguri: Seq[CastigTermic]):
  private val putereRecomandataCoeficient: Double = 1.15

  def totalCastiguri: Power   = castiguri.map(_.valoare).sum
  def putereRecomadata: Power = totalCastiguri * putereRecomandataCoeficient

class CalculatorSarcinaTermica(spatiu: Spatiu, parametriClimatici: ParametriClimatici):

  def calculeaza: RezultatCalcul =
    val castiguriTermice = Seq(
      CalculatorPeretiExterni.calculeaza(spatiu.peretiExterni, parametriClimatici),
      CalculatorFerestre.calculeaza(spatiu.ferestre, parametriClimatici),
      CalculatorPlafon.calculeaza(spatiu.plafon, parametriClimatici),
      CalculatorPersoane.calculeaza(spatiu.ocupanti, parametriClimatici),
      CalculatorEchipamente.calculeaza(spatiu.echipamente, parametriClimatici),
      CalculatorVentilatie.calculeaza(spatiu.suprafataPardoseala * spatiu.inaltime, parametriClimatici)
    )
    RezultatCalcul(castiguriTermice)

  def genereazaRaport(rezultat: RezultatCalcul): String =
    val introducere =
      """╔══════════════════════════════════════════════════════════════════════════════════════════════════╗
      |║                                                                                                  ║
      |║                    CALCULATOR SARCINĂ TERMICĂ DE RĂCIRE                                          ║
      |║                         conform I5-2022 și SR 6648-2                                             ║
      |║                                                                                                  ║
      |╚══════════════════════════════════════════════════════════════════════════════════════════════════╝
      |
      |Acest calculator determină sarcina termică de răcire pentru spațiul specificat,
      |situat la ultimul etaj, cu plafon expus la radiație solară, pentru luna iulie.
      |
      |Calculul include (conform I5-2022):
      |  • Câștiguri prin pereți externi (transmisie termică)
      |  • Câștiguri prin ferestre (transmisie + radiație solară)
      |  • Câștiguri prin plafon expus (cu supraîncălzire solară)
      |  • Câștiguri de la persoane (sensibil + latent)
      |  • Câștiguri de la echipamente electrocasnice (doar cele care funcționează în timpul zilei)
      |  • Câștiguri prin ventilație/infiltrații
      |
      |NOTĂ: Iluminatul nu este inclus deoarece nu funcționează în timpul zilei (ora de vârf termic).
      |
      |Radiația solară este calculată conform I5-2022, Anexa 3, folosind metoda orei critice:
      |  - Se determină ora la care radiația totală prin toate ferestrele este maximă
      |  - Se calculează câștigurile folosind valorile simultane la acea oră
      |  - Radiație totală = I_D (directă) + I_d (difuză)
      |""".stripMargin

    val raportDetaliat = genereazaRaportFormatat(rezultat)

    s"$introducere\n$raportDetaliat"

  private def formatNumber(value: Double, decimals: Int = 2): String =
    s"%.${decimals}f".format(value)

  private def formatPower(power: Power): String =
    f"${power.toWatts}%.2f W"

  private def formatIrradiance(irradiance: Irradiance): String =
    f"${irradiance.toWattsPerSquareMeter}%.2f W/m²"

  private def genereazaRaportFormatat(rezultat: RezultatCalcul): String =
    val volum            = spatiu.suprafataPardoseala * spatiu.inaltime
    val separator        = "=" * 100
    val separatorMinus   = "-" * 100
    val putereRecomadata = rezultat.putereRecomadata

    val antet = f"""
       |$separator
       |SPAȚIU: ${spatiu.nume}
       |Suprafață: ${spatiu.suprafataPardoseala}
       |Înălțime: ${spatiu.inaltime}
       |Volum: $volum
       |$separator""".stripMargin

    val parametri = f"""
       |
       |PARAMETRI CLIMATICI:
       |  Temperatură exterioară: ${parametriClimatici.temperaturaExterioara}
       |  Temperatură interioară: ${parametriClimatici.temperaturaInterioara}
       |  Diferență temperatură (ΔT): ${parametriClimatici.deltaTemperatura}""".stripMargin

    val castiguriText = rezultat.castiguri
      .map: castig =>
        val componente = castig.componente
          .map: c =>
            val parametriText = if c.parametri.nonEmpty then
              val params = c.parametri.map((k, v) => f"    $k%-25s: $v").mkString("\n")
              f"\n$params"
            else ""
            f"  ${c.nume}: ${formatPower(c.valoare)}$parametriText"
          .mkString("\n\n")
        f"""$separatorMinus
         |${castig.sursa}
         |Formula: ${castig.formula}
         |$separatorMinus
         |$componente
         |
         |TOTAL ${castig.sursa}: ${formatPower(castig.valoare)}""".stripMargin
      .mkString("\n")

    val rezumat = f"""$separator
       |REZUMAT CÂȘTIGURI TERMICE
       |$separator
       |${rezultat.castiguri.map(c => f"${c.sursa}%-50s: ${formatPower(c.valoare)}").mkString("\n")}
       |$separatorMinus
       |${"TOTAL CÂȘTIGURI TERMICE"}%-50s: ${formatPower(rezultat.totalCastiguri)}
       |$separator
       |
       |>>> PUTERE NECESARĂ INSTALAȚIE CLIMATIZARE: ${formatPower(rezultat.totalCastiguri)} <<<
       |
       |NOTĂ: Se recomandă adăugarea unui coeficient de siguranță de 10-15%% pentru dimensionarea instalației.
       |>>> PUTERE RECOMANDATĂ (cu 15%% siguranță): ${formatPower(rezultat.putereRecomadata)} <<<
       |""".stripMargin

    s"""$antet$parametri
       |$castiguriText
       |$rezumat""".stripMargin
