package vrf

import squants.energy.*
import squants.energy.PowerConversions.PowerNumeric
import squants.thermal.*

/** Acest modul conține logica generică pentru analiza capacității sistemelor VRF. Include:
  *   - Derating capacitate unitate externă în funcție de temperatură
  *   - Efectul expunerii la soare asupra temperaturii aerului aspirat
  *   - Analiza dimensionării unității externe față de unitățile interne
  *   - Factori de simultaneitate pentru sisteme VRF
  */

case class RezultatAnalizaSetPoint(
  tempInterioara: Temperature,
  factorSimultaneitate: Double,
  capacitateTotalaInterne: Power,
  necesarCuSimultaneitate: Power,
  factorCorectieCapacitate: Double,
  puncteAnaliza: List[PunctAnaliza]
)

case class PunctAnaliza(
  tempLaUmbra: Int,
  tempAerAspirat: Temperature,
  capacitateReala: Power,
  deficitProcent: Double
)

case class RezultatAnalizaVRF(
  unitateExterna: UnitateExterna,
  unitatiInterne: List[UnitateInterna],
  lungimeTevariEchivalentaM: Double,
  analize: List[RezultatAnalizaSetPoint]
):
  def capacitateTotalaInterne: Power   = unitatiInterne.map(_.capacitateNominala).sum
  def factorCorectieCapacitate: Double = unitateExterna.tabelFactoriCorectieTeviEL.factorLa(lungimeTevariEchivalentaM)

class CalculatorAnalizaVRF(
  unitateExterna: UnitateExterna,
  unitatiInterne: List[UnitateInterna],
  lungimeTevariEchivalentaM: Double,
  factoriSimultaneitate: List[Double]
):

  def calculeaza(): RezultatAnalizaVRF =
    val analize = for {
      setPoint  <- unitateExterna.temperaturiInterioareDisponibile
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
    val factorCorectie   = unitateExterna.tabelFactoriCorectieTeviEL.factorLa(lungimeTevariEchivalentaM)

    val puncte = (31 to 45).map { tempUmbra =>
      val tempLaUmbra         = Celsius(tempUmbra)
      val tempAerAspirat      = tempLaUmbra + unitateExterna.adaosTemperaturaSoare
      val capacitateDinManual = unitateExterna.capacitateDinTabel(tempAerAspirat, setPoint)
      val capacitateReala     = capacitateDinManual * factorCorectie
      val deficit             = ((necesar - capacitateReala) / necesar) * 100

      PunctAnaliza(tempUmbra, tempAerAspirat, capacitateReala, deficit)
    }.toList

    RezultatAnalizaSetPoint(setPoint, factorSim, capacitateTotala, necesar, factorCorectie, puncte)

  private def genereazaConcluziePentruFactor(factorSim: Double, analize: List[RezultatAnalizaSetPoint]): String =
    val Separator = "=" * 100
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
    val Separator = "=" * 100

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

    val analizePerFactor = rezultat.analize.groupBy(_.factorSimultaneitate).toList.sortBy { case (factor, _) => factor }

    val tabeleConConcluzii = analizePerFactor.map { case (factorSim, analize) =>
      val tabele    = analize.map(genereazaTabelPentruSetPoint).mkString("\n\n")
      val concluzie = genereazaConcluziePentruFactor(factorSim, analize)
      s"$tabele\n\n$concluzie"
    }

    s"""$headerGeneral
       |$listaUnitati
       |$totalInterne
       |${tabeleConConcluzii.mkString("\n\n")}
       |""".stripMargin

  private def genereazaTabelPentruSetPoint(analiza: RezultatAnalizaSetPoint): String =
    val Separator      = "=" * 100
    val SeparatorMinus = "-" * 100
    val procentaj      = (analiza.factorSimultaneitate * 100).toInt

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
