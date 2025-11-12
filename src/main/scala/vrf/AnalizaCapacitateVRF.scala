package vrf

import squants.thermal.*
import squants.thermal.TemperatureConversions.given
import squants.energy.*
import squants.energy.PowerConversions.given
import squants.energy.PowerConversions.PowerNumeric

/** Analiză capacitate sistem VRF - Core Logic
  *
  * Acest modul conține logica generică pentru analiza capacității sistemelor VRF. Include:
  *   - Derating capacitate unitate externă în funcție de temperatură
  *   - Efectul expunerii la soare asupra temperaturii aerului aspirat
  *   - Analiza dimensionării unității externe față de unitățile interne
  *   - Factori de simultaneitate pentru sisteme VRF
  *
  * Definițiile de tipuri (UnitateInterna, UnitateExterna, etc.) sunt în package object.
  */

// ============================================================================
// REZULTATE CALCULE
// ============================================================================

/** Rezultat al analizei de capacitate VRF pentru un set point dat */
case class RezultatAnalizaSetPoint(
  tempInterioara: Temperature,
  capacitateTotalaInterne: Power,
  necesarSimultaneitate75: Power,
  necesarSimultaneitate100: Power,
  factorCorectieCapacitate: Double,
  randuri: List[RandTabelAnaliza]
)

/** Un rând din tabelul de analiză */
case class RandTabelAnaliza(
  tempLaUmbra: Int,
  tempAerAspirat: Temperature,
  capacitateReala: Power,
  deficit75Procent: Double,
  deficit100Procent: Double,
  marker75: String,
  marker100: String
)

/** Rezultat complet al analizei VRF cu toate set points */
case class RezultatAnalizaVRF(
  unitateExterna: UnitateExterna,
  unitatiInterne: List[UnitateInterna],
  lungimeTevariEchivalentaM: Double,
  analize: List[RezultatAnalizaSetPoint]
):
  def capacitateTotalaInterne: Power   = unitatiInterne.map(_.capacitateNominala).sum
  def factorCorectieCapacitate: Double = unitateExterna.tabelFactoriCorectieTeviEL.factorLa(lungimeTevariEchivalentaM)

// ============================================================================
// CALCULATOR ANALIZĂ VRF
// ============================================================================

class CalculatorAnalizaVRF(
  unitateExterna: UnitateExterna,
  unitatiInterne: List[UnitateInterna],
  lungimeTevariEchivalentaM: Double
):

  /** Pasul 1: Calculează analiza de capacitate pentru toate set points
    *
    * Returnează rezultatul calculului cu toate analizele detaliate.
    */
  def calculeaza(): RezultatAnalizaVRF =
    val analize = unitateExterna.temperaturiInterioareDisponibile.map { tempInt =>
      calculeazaPentruSetPoint(tempInt)
    }
    RezultatAnalizaVRF(unitateExterna, unitatiInterne, lungimeTevariEchivalentaM, analize)

  private def calculeazaPentruSetPoint(tempInterioara: Temperature): RezultatAnalizaSetPoint =
    val capacitateTotala           = unitatiInterne.map(_.capacitateNominala).sum
    val necesarCuSimultaneitate75  = capacitateTotala * 0.75
    val necesarCuSimultaneitate100 = capacitateTotala
    val factorCorectie             = unitateExterna.tabelFactoriCorectieTeviEL.factorLa(lungimeTevariEchivalentaM)

    val randuri = (31 to 45).map { tempUmbra =>
      val tempLaUmbra         = Celsius(tempUmbra)
      val tempAerAspirat      = tempLaUmbra + unitateExterna.adaosTemperaturaSoare
      val capacitateDinManual = unitateExterna.capacitateDinTabel(tempAerAspirat, tempInterioara)
      val capacitateReala     = capacitateDinManual * factorCorectie

      // Marker pentru deficit 75%: problematic când deficit > 0 sau temp > max
      val deficit75 = ((necesarCuSimultaneitate75 - capacitateReala) / necesarCuSimultaneitate75) * 100
      val marker75  = if deficit75 > 0 || tempAerAspirat >= unitateExterna.temperaturaMaxima then "⚠" else " "

      // Marker pentru deficit 100%: problematic când deficit > 25%
      val deficit100 = ((necesarCuSimultaneitate100 - capacitateReala) / necesarCuSimultaneitate100) * 100
      val marker100  = if deficit100 > 25 || tempAerAspirat >= unitateExterna.temperaturaMaxima then "⚠" else " "

      RandTabelAnaliza(tempUmbra, tempAerAspirat, capacitateReala, deficit75, deficit100, marker75, marker100)
    }.toList

    RezultatAnalizaSetPoint(
      tempInterioara,
      capacitateTotala,
      necesarCuSimultaneitate75,
      necesarCuSimultaneitate100,
      factorCorectie,
      randuri
    )

  /** Pasul 2: Generează raport text formatat din rezultatul calculului */
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

    val totalInterne =
      f"""  ${"-" * 60}
         |  TOTAL CAPACITATE UNITĂȚI INTERNE:                ${rezultat.capacitateTotalaInterne.toKilowatts}%5.1f kW
         |
         |UNITATE EXTERNĂ:
         |  • ${unitateExterna.model}%-45s: ${unitateExterna.putereNominala.toKilowatts}%5.1f kW (nominal la ${unitateExterna.temperaturaNominala.toCelsiusScale}%.0f°C / 27°C interior)
         |
         |NOTĂ IMPORTANTĂ:
         |  • Temperatura maximă de ${unitateExterna.temperaturaMaxima.toCelsiusScale}%.0f°C se referă la temperatura aerului aspirat (care intră în compresor)
         |  • Unitatea montată pe bloc, în soare, are temperatura aerului aspirat cu ~${unitateExterna.adaosTemperaturaSoare.toCelsiusDegrees}%.0f°C mai mare
         |    decât temperatura la umbră (datorită radiației solare și reflexiei de la suprafețe)
         |  • Factor de corecție capacitate (țevi, înălțime): ${rezultat.factorCorectieCapacitate}%.2f
         |""".stripMargin

    val tabele = rezultat.analize.map { analiza => genereazaTabelPentruSetPoint(analiza) }
    s"""$headerGeneral
       |$listaUnitati
       |$totalInterne
       |${tabele.mkString("\n\n")}
       |""".stripMargin

  private def genereazaTabelPentruSetPoint(analiza: RezultatAnalizaSetPoint): String =
    val Separator      = "=" * 100
    val SeparatorMinus = "-" * 100

    val header = f"""
         |$Separator
         |ANALIZA CAPACITATE - SET POINT INTERIOR: ${analiza.tempInterioara}
         |$Separator
         |
         |CAPACITATE NECESARĂ:
         |  • Capacitate totală unități interne: ${analiza.capacitateTotalaInterne.toKilowatts}%.2f kW
         |  • Necesar cu simultaneitate 75%%: ${analiza.necesarSimultaneitate75.toKilowatts}%.2f kW
         |  • Necesar cu simultaneitate 100%%: ${analiza.necesarSimultaneitate100.toKilowatts}%.2f kW
         |
         |LEGENDĂ:
         |  • Deficit 75%%: Deficit față de necesarul cu simultaneitate 75%% (⚠ = problematic)
         |  • Deficit 100%%: Deficit față de necesarul cu simultaneitate 100%% (⚠ = problematic când > 25%%)
         |  • Temp. aer aspirat = Temp. la umbră + ${unitateExterna.adaosTemperaturaSoare.toCelsiusDegrees}%.0f°C (efect soare)
         |  • Capacitate reală = Capacitate din tabel × ${analiza.factorCorectieCapacitate}%.2f (factor corecție țevi)
         |$SeparatorMinus""".stripMargin

    val tabelHeader = """
         | Temp. la  │  Temp. aer   │  Capacitate  │    Deficit     │    Deficit
         |  umbră    │   aspirat    │    reală     │ simultaneitate │ simultaneitate
         |   (°C)    │     (°C)     │     (kW)     │      75%       │      100%
         |-----------+--------------+--------------+----------------+---------------"""

    val randuri = analiza.randuri
      .map { rand =>
        f"   ${rand.tempLaUmbra}%5d   │    ${rand.tempAerAspirat.toCelsiusScale}%5.1f     │   ${rand.capacitateReala.toKilowatts}%6.2f     │  ${rand.marker75} ${rand.deficit75Procent}%6.1f%%     │  ${rand.marker100} ${rand.deficit100Procent}%6.1f%%"
      }
      .mkString("\n")

    s"$header$tabelHeader\n$randuri\n$SeparatorMinus"

object AnalizaCapacitateVRF:

  def genereazaRaportComplet(
    unitateExterna: UnitateExterna,
    unitatiInterne: List[UnitateInterna],
    lungimeTeviEchivalenta: Double
  ): String =
    val calculator = CalculatorAnalizaVRF(unitateExterna, unitatiInterne, lungimeTeviEchivalenta)
    val rezultat   = calculator.calculeaza()
    calculator.genereazaRaport(rezultat)
