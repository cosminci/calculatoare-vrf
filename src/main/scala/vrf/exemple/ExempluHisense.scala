package vrf.exemple

import squants.thermal.*
import squants.thermal.TemperatureConversions.given
import squants.energy.*
import squants.energy.PowerConversions.given
import vrf.*

object ExempluHisense:

  /** Tabel de capacitate pentru set point 22°C (Indoor 22°C DB / 16°C WB) Conform Hisense/Cirelius Technical & Service
    * Manual V5.0 - COOLING CAPACITY (W)
    */
  private val tabelCapacitate22C = TabelCapacitate(
    tempInterioaraSetPoint = 22.celsius,
    puncte = List(
      (15.celsius, 10.25.kilowatts),
      (20.celsius, 10.625.kilowatts),
      (25.celsius, 10.75.kilowatts),
      (30.celsius, 11.125.kilowatts),
      (35.celsius, 11.25.kilowatts),
      (40.celsius, 10.0.kilowatts),
      (45.celsius, 8.375.kilowatts),
      (48.celsius, 8.0.kilowatts)
    )
  )

  /** Tabel de capacitate pentru set point 27°C (Indoor 27°C DB / 19°C WB) Conform Hisense/Cirelius Technical & Service
    * Manual V5.0 - COOLING CAPACITY (W)
    */
  private val tabelCapacitate27C = TabelCapacitate(
    tempInterioaraSetPoint = 27.celsius,
    puncte = List(
      (15.celsius, 13.25.kilowatts),
      (20.celsius, 13.125.kilowatts),
      (25.celsius, 13.0.kilowatts),
      (30.celsius, 12.75.kilowatts),
      (35.celsius, 12.5.kilowatts),
      (40.celsius, 11.25.kilowatts),
      (45.celsius, 9.125.kilowatts),
      (48.celsius, 8.75.kilowatts)
    )
  )

  /** Tabel de factori de corecție pentru lungimea echivalentă a țevilor (EL) Conform manualului tehnic pentru modelul
    * 42K Puncte limită din tabel:
    *   - ≤25m: 1.00 (fără penalizare)
    *   - >80m: 0.70 (capacitate foarte redusă)
    */
  private val tabelFactoriCorectieTeviEL = TabelFactoriCorectieTeviEL(
    puncte = List(
      (0.0, 1.00),
      (25.0, 1.00),
      (30.0, 0.98),
      (40.0, 0.94),
      (45.0, 0.93),
      (50.0, 0.90),
      (60.0, 0.85),
      (70.0, 0.75),
      (80.0, 0.72),
      (100.0, 0.70)
    )
  )

  private val unitateExternaHisense = UnitateExterna(
    model = "Hisense 5AMW125U4RTA (42K)",
    putereNominala = 12.5.kilowatts,
    temperaturaNominala = 35.celsius,
    temperaturaMaxima = 48.celsius,
    tabeleCapacitate = Map(22.celsius -> tabelCapacitate22C, 27.celsius -> tabelCapacitate27C),
    tabelFactoriCorectieTeviEL = tabelFactoriCorectieTeviEL,
    adaosTemperaturaSoare = 4.celsius, // Unitate montată pe bloc, în soare pe tot parcursul zilei
    manual = "Hisense/Cirelius Technical & Service Manual V5.0 - Multi-Split Type Air Conditioners (R32)"
  )

  private val unitatiInterneHisense = List(
    UnitateInterna("Hisense ADT71UX4RCL4 (Open Space)", 7.2.kilowatts),
    UnitateInterna("Hisense ADT35UX4RBL4 (Dormitor 1)", 3.5.kilowatts),
    UnitateInterna("Hisense ADT26UX4RBL4 (Dormitor 2)", 2.9.kilowatts),
    UnitateInterna("Hisense ADT26UX4RBL4 (Birou)", 2.9.kilowatts)
  )

  /** Lungimea echivalentă (EL) include:
    *   - Lungimea reală a țevilor (L)
    *   - Penalizarea pentru coturi 90° (din tabel: 0.2m/cot pentru Ø12.7mm)
    *   - Penalizarea pentru diferența de înălțime (H)
    *
    * Formula: EL = L + Σ(penalizări_coturi) + factor × H
    *
    * Considerăm factorul de penalizare pentru acest exemplu ca 1.0
    */
  val lungimeTeviEchivalenta = 25.0 // Lungime echivalentă totală (include L + coturi + H)

  def main(args: Array[String]): Unit =
    val raport = AnalizaCapacitateVRF.genereazaRaportComplet(
      unitateExterna = unitateExternaHisense,
      unitatiInterne = unitatiInterneHisense,
      lungimeTeviEchivalenta = lungimeTeviEchivalenta,
      factoriSimultaneitate = List(0.6, 0.7, 0.8, 0.9)
    )
    println(raport)
