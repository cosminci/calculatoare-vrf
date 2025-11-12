import squants.thermal.*
import squants.thermal.TemperatureConversions.given
import squants.energy.*
import squants.energy.PowerConversions.given

package object vrf:

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
  )

  /** Tabel de factori de corecție pentru lungimea țevilor
    *
    * @param puncte
    *   lista de puncte (lungime_m, factor) din tabelul producătorului
    */
  case class TabelFactoriCorectieTeviEL(
    puncte: List[(Double, Double)]
  )

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
  )
