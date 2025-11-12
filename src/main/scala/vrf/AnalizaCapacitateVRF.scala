package vrf

import squants.energy.*
import squants.energy.PowerConversions.PowerNumeric
import squants.thermal.*

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
    val analizeSortate = analize.sortBy(_.tempInterioara.toCelsiusScale)(Ordering[Double].reverse)
    RezultatAnalizaVRF(unitateExterna, unitatiInterne, lungimeTevariEchivalentaM, analizeSortate)

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

  /** Generează concluzii privind simultaneitatea 75% */
  private def genereazaConcluzii(rezultat: RezultatAnalizaVRF): String =
    val Separator = "=" * 100

    val concluzii = rezultat.analize.map { analiza =>
      // Găsim prima temperatură la umbră unde apare deficit (deficit75 > 0)
      val primulDeficit = analiza.randuri.find(_.deficit75Procent > 0)

      primulDeficit match
        case Some(rand) =>
          f"  • Set point ${analiza.tempInterioara}: La ${rand.tempLaUmbra}°C la umbră (${rand.tempAerAspirat} aer aspirat), deficit de ${rand.deficit75Procent}%.1f%%"
        case None =>
          f"  • Set point ${analiza.tempInterioara}: Capacitate suficientă la toate temperaturile analizate"
    }.mkString("\n")

    f"""
       |$Separator
       |CONCLUZII - SIMULTANEITATE 75%%
       |$Separator
       |
       |Prima apariție a deficitului de capacitate (simultaneitate 75%%):
       |$concluzii
       |
       |NOTĂ: Aceste valori indică temperatura exterioară la umbră de la care sistemul VRF
       |      nu mai poate asigura capacitatea necesară cu simultaneitate 75%%.
       |""".stripMargin

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

    // Folosim prima analiză pentru a obține valorile (sunt identice pentru toate set points)
    val primaAnaliza = rezultat.analize.head

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
         |  • Capacitate totală unități interne: ${primaAnaliza.capacitateTotalaInterne.toKilowatts}%.2f kW
         |  • Necesar cu simultaneitate 75%%: ${primaAnaliza.necesarSimultaneitate75.toKilowatts}%.2f kW
         |  • Necesar cu simultaneitate 100%%: ${primaAnaliza.necesarSimultaneitate100.toKilowatts}%.2f kW
         |
         |LEGENDĂ:
         |  • Deficit 75%%: Deficit față de necesarul cu simultaneitate 75%% (⚠ = problematic)
         |  • Deficit 100%%: Deficit față de necesarul cu simultaneitate 100%% (⚠ = problematic când > 25%%)
         |  • Temp. aer aspirat = Temp. la umbră + ${unitateExterna.adaosTemperaturaSoare.toCelsiusDegrees}%.0f°C (efect soare)
         |  • Capacitate reală = Capacitate din tabel × ${primaAnaliza.factorCorectieCapacitate}%.2f (factor corecție țevi)
         |""".stripMargin

    val tabele = rezultat.analize.map { analiza => genereazaTabelPentruSetPoint(analiza) }
    val concluzii = genereazaConcluzii(rezultat)

    s"""$headerGeneral
       |$listaUnitati
       |$totalInterne
       |${tabele.mkString("\n\n")}
       |$concluzii
       |""".stripMargin

  private def genereazaTabelPentruSetPoint(analiza: RezultatAnalizaSetPoint): String =
    val Separator      = "=" * 100
    val SeparatorMinus = "-" * 100

    val header = f"""
         |$Separator
         |ANALIZĂ CAPACITATE - SET POINT INTERIOR: ${analiza.tempInterioara}
         |$Separator""".stripMargin

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

    s"$header\n$tabelHeader\n$randuri\n$SeparatorMinus"

object AnalizaCapacitateVRF:

  def genereazaRaportComplet(
    unitateExterna: UnitateExterna,
    unitatiInterne: List[UnitateInterna],
    lungimeTeviEchivalenta: Double
  ): String =
    val calculator = CalculatorAnalizaVRF(unitateExterna, unitatiInterne, lungimeTeviEchivalenta)
    val rezultat   = calculator.calculeaza()
    calculator.genereazaRaport(rezultat)
