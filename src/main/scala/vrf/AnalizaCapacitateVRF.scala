package vrf

import squants.energy.*
import squants.energy.PowerConversions.{PowerNumeric, PowerConversions}
import squants.thermal.*
import common.{Separator, SeparatorMinus}

/** Acest modul conține logica generică pentru analiza capacității sistemelor VRF. Include:
  *   - Derating capacitate unitate externă în funcție de temperatură
  *   - Efectul expunerii la soare asupra temperaturii aerului aspirat
  *   - Analiza dimensionării unității externe față de unitățile interne
  *   - Factori de simultaneitate pentru sisteme VRF
  */

private case class PunctAnaliza(
  tempLaUmbra: Int,
  tempAerAspirat: Temperature,
  capacitateReala: Power,
  deficitProcent: Double
)

private case class RezultatAnalizaSetPoint(
  tempInterioara: Temperature,
  factorSimultaneitate: Double,
  capacitateTotalaInterne: Power,
  necesarCuSimultaneitate: Power,
  factorCorectieCapacitate: Double,
  puncteAnaliza: List[PunctAnaliza]
)

private case class RezultatAnalizaVRF(
  unitateExterna: UnitateExterna,
  unitatiInterne: List[UnitateInterna],
  lungimeTevariEchivalentaM: Double,
  analize: List[RezultatAnalizaSetPoint]
):
  def capacitateTotalaInterne: Power = unitatiInterne.map(_.capacitateNominala).sum
  def factorCorectieCapacitate: Double =
    Interpolare.factorCorectieTeviLa(unitateExterna.tabelFactoriCorectieTeviEL, lungimeTevariEchivalentaM)

class CalculatorAnalizaVRF(
  unitateExterna: UnitateExterna,
  unitatiInterne: List[UnitateInterna],
  lungimeTevariEchivalentaM: Double,
  factoriSimultaneitate: List[Double]
):

  /** Returnează toate temperaturile interioare (set points) disponibile în tabele */
  private def temperaturiInterioareDisponibile: List[Temperature] =
    unitateExterna.tabeleCapacitate.keys.toList.sortBy(_.toCelsiusScale)

  /** Capacitate de răcire din tabelul manual la diferite temperaturi */
  private def capacitateDinTabel(tempExterioara: Temperature, tempInterioara: Temperature): Power =
    unitateExterna.tabeleCapacitate.get(tempInterioara) match
      case Some(tabel) => Interpolare.capacitateLa(tabel, tempExterioara)
      case None =>
        throw new IllegalArgumentException(
          s"Temperatura interioară ${tempInterioara.toCelsiusScale}°C nu există în tabele. " +
            s"Temperaturi disponibile: ${temperaturiInterioareDisponibile.map(_.toCelsiusScale).mkString(", ")}°C"
        )

  /** Capacitate reală de răcire ținând cont de derating, soare și corecție țevi */
  private def capacitateReala(
    tempLaUmbra: Temperature,
    tempInterioara: Temperature,
    lungimeTevariEchivalentaM: Double
  ): Power =
    val tempAerAspirat      = tempLaUmbra + unitateExterna.adaosTemperaturaSoare
    val capacitateDinManual = capacitateDinTabel(tempAerAspirat, tempInterioara)
    val factorCorectie =
      Interpolare.factorCorectieTeviLa(unitateExterna.tabelFactoriCorectieTeviEL, lungimeTevariEchivalentaM)
    capacitateDinManual * factorCorectie

  def calculeaza(): RezultatAnalizaVRF =
    val analize = for {
      setPoint  <- temperaturiInterioareDisponibile
      factorSim <- factoriSimultaneitate
    } yield calculeazaPentruSetPoint(setPoint, factorSim)

    val analizeSortate = analize.sortBy(a => (-a.tempInterioara.toCelsiusScale, a.factorSimultaneitate))
    RezultatAnalizaVRF(unitateExterna, unitatiInterne, lungimeTevariEchivalentaM, analizeSortate)

  private def calculeazaPentruSetPoint(
    setPoint: Temperature,
    factorSim: Double
  ): RezultatAnalizaSetPoint =
    val capacitateTotala = unitatiInterne.map(_.capacitateNominala).sum
    val necesar          = capacitateTotala * factorSim
    val factorCorectie =
      Interpolare.factorCorectieTeviLa(unitateExterna.tabelFactoriCorectieTeviEL, lungimeTevariEchivalentaM)

    val puncte = (31 to 45).map { tempUmbra =>
      val tempLaUmbra     = Celsius(tempUmbra)
      val tempAerAspirat  = tempLaUmbra + unitateExterna.adaosTemperaturaSoare
      val capacitateReala = this.capacitateReala(tempLaUmbra, setPoint, lungimeTevariEchivalentaM)
      val deficit         = ((necesar - capacitateReala) / necesar) * 100

      PunctAnaliza(tempUmbra, tempAerAspirat, capacitateReala, deficit)
    }.toList

    RezultatAnalizaSetPoint(setPoint, factorSim, capacitateTotala, necesar, factorCorectie, puncte)

  private def genereazaConcluziePentruFactor(factorSim: Double, analize: List[RezultatAnalizaSetPoint]): String =
    val procentaj = (factorSim * 100).toInt

    val concluzii = analize
      .map { analiza =>
        val primulDeficit = analiza.puncteAnaliza.find(_.deficitProcent > 0)
        primulDeficit match
          case Some(punct) =>
            f"  • Set point ${analiza.tempInterioara}: La ${punct.tempLaUmbra}°C la umbră (${punct.tempAerAspirat} aer aspirat), deficit de ${punct.deficitProcent}%.1f%%"
          case None =>
            f"  • Set point ${analiza.tempInterioara}: Capacitate suficientă la toate temperaturile analizate"
      }
      .mkString("\n")

    f"""$Separator
       |CONCLUZII - SIMULTANEITATE $procentaj%%
       |$Separator
       |
       |Prima apariție a deficitului de capacitate:
       |$concluzii
       |
       |NOTĂ: Aceste valori indică temperatura exterioară la umbră de la care sistemul VRF nu mai poate asigura capacitatea necesară.""".stripMargin

  def genereazaRaport(rezultat: RezultatAnalizaVRF): String =
    val headerGeneral =
      f"""
         |$Separator
         |ANALIZA DIMENSIONARE SISTEM VRF
         |$Separator
         |
         |SURSĂ DATE: ${unitateExterna.manual}
         |
         |UNITĂȚI INTERNE INSTALATE:""".stripMargin

    val listaUnitati = unitatiInterne
      .map { u => f"  • ${u.nume}%-45s: ${u.capacitateNominala.toKilowatts}%5.1f kW" }
      .mkString("\n")

    val factoriSimInfo = factoriSimultaneitate
      .map { factor =>
        val procentaj = (factor * 100).toInt
        val necesar   = rezultat.capacitateTotalaInterne * factor
        val label     = f"Necesar cu simultaneitate $procentaj%%"
        f"  • $label%-30s: ${necesar.toKilowatts}%.2f kW"
      }
      .mkString("\n")

    val capacitataInterna = rezultat.analize.head.capacitateTotalaInterne
    val factorCorectie    = rezultat.analize.head.factorCorectieCapacitate
    val totalInterne =
      f"""  ${"-" * 60}
         |  ${"TOTAL CAPACITATE UNITĂȚI INTERNE"}%-47s: ${rezultat.capacitateTotalaInterne.toKilowatts}%5.1f kW
         |
         |UNITATE EXTERNĂ:
         |  • ${unitateExterna.model}%-45s: ${unitateExterna.putereNominala.toKilowatts}%5.1f kW (nominal la ${unitateExterna.temperaturaNominala.toCelsiusScale}%.0f°C / 27°C interior)
         |
         |NOTĂ IMPORTANTĂ:
         |  • Temperatura maximă de ${unitateExterna.temperaturaMaxima.toCelsiusScale}%.0f°C se referă la temperatura aerului aspirat (care intră în compresor)
         |  • Unitatea montată pe bloc, în soare, are temperatura aerului aspirat cu ~${unitateExterna.adaosTemperaturaSoare.toCelsiusDegrees}%.0f°C mai mare
         |    decât temperatura la umbră (datorită radiației solare și reflexiei de la suprafețe)
         |  • Factor de corecție capacitate (țevi, înălțime): ${rezultat.factorCorectieCapacitate}%.2f
         |
         |CAPACITATE NECESARĂ:
         |  • Capacitate totală unități interne: ${capacitataInterna.toKilowatts}%.2f kW
         |$factoriSimInfo
         |
         |LEGENDĂ:
         |  • Deficit: Deficit față de necesarul cu simultaneitate (⚠ = problematic când > 0)
         |  • Temp. aer aspirat = Temp. la umbră + ${unitateExterna.adaosTemperaturaSoare.toCelsiusDegrees}%.0f°C (efect soare)
         |  • Capacitate reală = Capacitate din tabel × $factorCorectie%.2f (factor corecție țevi)
         |""".stripMargin

    val tabeleCapacitate = genereazaTabeleleCapacitate()
    val analizePerFactor = rezultat.analize.groupBy(_.factorSimultaneitate).toList.sortBy { case (factor, _) => factor }

    val tabeleConConcluzii = analizePerFactor.map { case (factorSim, analize) =>
      val tabeleAnaliza = analize.map(genereazaTabelPentruSetPoint).mkString("\n\n")
      val concluzie     = genereazaConcluziePentruFactor(factorSim, analize)
      s"$tabeleAnaliza\n\n$concluzie"
    }

    s"""$headerGeneral
       |$listaUnitati
       |$totalInterne
       |$tabeleCapacitate
       |
       |${tabeleConConcluzii.mkString("\n\n")}
       |""".stripMargin

  private def genereazaTabelPentruSetPoint(analiza: RezultatAnalizaSetPoint): String =
    val procentaj = (analiza.factorSimultaneitate * 100).toInt

    val header = f"""
         |$Separator
         |ANALIZĂ CAPACITATE - SET POINT INTERIOR: ${analiza.tempInterioara} - SIMULTANEITATE: $procentaj%%
         |$Separator""".stripMargin

    val tabelHeader = f"""
         | Temp. la  │  Temp. aer   │  Capacitate  │    Deficit
         |  umbră    │   aspirat    │    reală     │ simultaneitate
         |   (°C)    │     (°C)     │     (kW)     │     $procentaj%%
         |-----------+--------------+--------------+---------------"""

    val randuri = analiza.puncteAnaliza
      .map { punct =>
        // Marker pentru deficit: problematic când deficit > 0 sau temp > max
        val marker =
          if punct.deficitProcent > 0 || punct.tempAerAspirat >= unitateExterna.temperaturaMaxima then "⚠" else " "
        f"   ${punct.tempLaUmbra}%5d   │    ${punct.tempAerAspirat.toCelsiusScale}%5.1f     │   ${punct.capacitateReala.toKilowatts}%6.2f     │  $marker ${punct.deficitProcent}%6.1f%%"
      }
      .mkString("\n")

    s"$header\n$tabelHeader\n$randuri\n$SeparatorMinus"

  private def genereazaTabeleleCapacitate(): String =
    unitateExterna.tabeleCapacitate.toList
      .sortBy { case (setPoint, _) => -setPoint.toCelsiusScale }
      .map { case (setPoint, tabelCapacitate) =>
        val header = f"""
             |$Separator
             |CAPACITATE DE RĂCIRE (din manual) - SET POINT INTERIOR: $setPoint
             |$Separator""".stripMargin

        val puncteSortate = tabelCapacitate.puncte.sortBy { case (temp, _) => temp.toCelsiusScale }

        val temperaturi    = puncteSortate.map { case (temp, _) => f"${temp.toCelsiusScale}%6.1f" }.mkString(" │ ")
        val randTemp       = f"Temp. aer aspirat (°C) │ $temperaturi"
        val capacitati     = puncteSortate.map { case (_, c) => f"${c.toKilowatts}%6.2f" }.mkString(" │ ")
        val randCapacitate = f"Capacitate răcire (kW) │ $capacitati"

        s"$header\n$randTemp\n${"-" * randTemp.length}\n$randCapacitate\n$SeparatorMinus"
      }
      .mkString("\n\n")

private object Interpolare:

  /** Interpolare liniară pentru capacitate din tabel */
  def capacitateLa(tabel: TabelCapacitate, tempExt: Temperature): Power =
    val sorted = tabel.puncte.sortBy { case (temperatura, _) => temperatura.toCelsiusScale }

    val (primaTemperatura, primaCapacitate)   = sorted.head
    val (ultimaTemperatura, ultimaCapacitate) = sorted.last

    if tempExt <= primaTemperatura then primaCapacitate
    else if tempExt >= ultimaTemperatura then ultimaCapacitate
    else
      // Găsim cele două puncte între care se află temperatura și interpolăm
      val (temp1, capacitate1, temp2, capacitate2) = sorted
        .sliding(2)
        .collectFirst { case List((t1, c1), (t2, c2)) if tempExt >= t1 && tempExt <= t2 => ((t1, c1), (t2, c2)) }
        .map { case ((t1, c1), (t2, c2)) => (t1.toCelsiusScale, c1.toKilowatts, t2.toCelsiusScale, c2.toKilowatts) }
        .get

      (capacitate1 + (tempExt.toCelsiusScale - temp1) * (capacitate2 - capacitate1) / (temp2 - temp1)).kilowatts

  /** Interpolare liniară pentru factor de corecție țevi */
  def factorCorectieTeviLa(tabel: TabelFactoriCorectieTeviEL, lungimeM: Double): Double =
    val sorted                         = tabel.puncte.sortBy { case (lungime, _) => lungime }
    val (primaLungime, primulFactor)   = sorted.head
    val (ultimaLungime, ultimulFactor) = sorted.last

    if lungimeM <= primaLungime then primulFactor
    else if lungimeM >= ultimaLungime then ultimulFactor
    else
      // Găsim cele două puncte între care se află lungimea și interpolăm
      val (lungime1, factor1, lungime2, factor2) = sorted
        .sliding(2)
        .collectFirst { case List((l1, f1), (l2, f2)) if lungimeM >= l1 && lungimeM <= l2 => (l1, f1, l2, f2) }
        .get

      factor1 + (lungimeM - lungime1) * (factor2 - factor1) / (lungime2 - lungime1)

object AnalizaCapacitateVRF:

  def genereazaRaportComplet(
    unitateExterna: UnitateExterna,
    unitatiInterne: List[UnitateInterna],
    lungimeTeviEchivalenta: Double,
    factoriSimultaneitate: List[Double]
  ): String =
    val calculator = CalculatorAnalizaVRF(unitateExterna, unitatiInterne, lungimeTeviEchivalenta, factoriSimultaneitate)
    val rezultat   = calculator.calculeaza()
    calculator.genereazaRaport(rezultat)
