import squants.thermal.*
import squants.thermal.TemperatureConversions.given
import squants.energy.*
import squants.energy.PowerConversions.given

/** Package object pentru modulul de analiză capacitate VRF
  *
  * Conține definițiile de tipuri și case class-uri comune folosite în analiză.
  */
package object vrf:

  // ============================================================================
  // UNITĂȚI VRF
  // ============================================================================

  /** Reprezentarea unei unități interne VRF */
  case class UnitateInterna(
    nume: String,
    capacitateNominala: Power
  )

  /** Tabel de capacitate pentru o anumită temperatură interioară (set point)
    *
    * @param tempInterioaraSetPoint
    *   temperatura interioară pentru care este valid tabelul (ex: 22°C sau 27°C)
    * @param puncte
    *   lista de puncte (temperatură exterioară, capacitate) din tabelul producătorului
    */
  case class TabelCapacitate(
    tempInterioaraSetPoint: Temperature,
    puncte: List[(Temperature, Power)]
  ):
    /** Calculează capacitatea prin interpolare liniară pentru o temperatură exterioară dată */
    def capacitateLa(tempExt: Temperature): Power =
      val sorted = puncte.sortBy { case (temperatura, _) => temperatura.toCelsiusScale }

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

  /** Tabel de factori de corecție pentru lungimea țevilor
    *
    * @param puncte
    *   lista de puncte (lungime_m, factor) din tabelul producătorului
    */
  case class TabelFactoriCorectieTeviEL(
    puncte: List[(Double, Double)]
  ):
    /** Calculează factorul de corecție pentru o lungime dată prin interpolare */
    def factorLa(lungimeM: Double): Double =
      val sorted                         = puncte.sortBy { case (lungime, _) => lungime }
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

  /** Reprezentarea unei unități externe VRF cu tabele de capacitate parametrizate
    *
    * @param model
    *   modelul unității externe
    * @param putereNominala
    *   puterea nominală la condițiile de referință
    * @param temperaturaNominala
    *   temperatura exterioară de referință
    * @param temperaturaMaxima
    *   temperatura maximă de funcționare (a aerului aspirat)
    * @param tabeleCapacitate
    *   map de la temperatura interioară (set point) la tabelul de capacitate corespunzător
    * @param tabelFactoriCorectieTeviEL
    *   tabelul de factori de corecție pentru lungimea echivalentă a țevilor
    * @param adaosTemperaturaSoare
    *   adaos de temperatură datorat expunerii la soare (specific montajului unității externe)
    * @param manual
    *   sursa datelor tehnice (manualul producătorului)
    */
  case class UnitateExterna(
    model: String,
    putereNominala: Power,
    temperaturaNominala: Temperature,
    temperaturaMaxima: Temperature,
    tabeleCapacitate: Map[Temperature, TabelCapacitate],
    tabelFactoriCorectieTeviEL: TabelFactoriCorectieTeviEL,
    adaosTemperaturaSoare: Temperature,
    manual: String
  ):

    /** Returnează toate temperaturile interioare (set points) disponibile în tabele */
    def temperaturiInterioareDisponibile: List[Temperature] = tabeleCapacitate.keys.toList.sortBy(_.toCelsiusScale)

    /** Capacitate de răcire din tabelul manual la diferite temperaturi
      *
      * @param tempExterioara
      *   temperatura exterioară
      * @param tempInterioara
      *   temperatura interioară (set point) - trebuie să existe în tabeleCapacitate
      */
    def capacitateDinTabel(tempExterioara: Temperature, tempInterioara: Temperature): Power =
      tabeleCapacitate.get(tempInterioara) match
        case Some(tabel) => tabel.capacitateLa(tempExterioara)
        case None =>
          throw new IllegalArgumentException(
            s"Temperatura interioară ${tempInterioara.toCelsiusScale}°C nu există în tabele. " +
              s"Temperaturi disponibile: ${temperaturiInterioareDisponibile.map(_.toCelsiusScale).mkString(", ")}°C"
          )

    /** Capacitate reală de răcire ținând cont de:
      *   - Derating din tabel în funcție de temperatură
      *   - Efectul expunerii la soare (adaos temperatură)
      *   - Factorul de corecție pentru lungimea țevilor
      *
      * @param tempLaUmbra
      *   temperatura exterioară la umbră
      * @param tempInterioara
      *   temperatura interioară (set point)
      * @param lungimeTevariEchivalentaM
      *   lungimea echivalentă a țevilor în metri
      */
    def capacitateReala(
      tempLaUmbra: Temperature,
      tempInterioara: Temperature,
      lungimeTevariEchivalentaM: Double
    ): Power =
      val tempAerAspirat      = tempLaUmbra + adaosTemperaturaSoare
      val capacitateDinManual = capacitateDinTabel(tempAerAspirat, tempInterioara)
      val factorCorectie      = tabelFactoriCorectieTeviEL.factorLa(lungimeTevariEchivalentaM)
      capacitateDinManual * factorCorectie
