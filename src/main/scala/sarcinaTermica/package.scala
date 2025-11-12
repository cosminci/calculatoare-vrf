import squants.thermal.*
import squants.space.*
import squants.energy.*

package object sarcinaTermica:

  type ConductantaTermica = Double // W/(m²·K)

  case class ParametriClimatici(
    temperaturaExterioara: Temperature,
    temperaturaInterioara: Temperature
  ):
    def deltaTemperatura: Temperature = temperaturaExterioara - temperaturaInterioara

  enum Orientare:
    case N, NE, E, SE, S, SV, V, NV

  /** Fereastră cu caracteristici termice și solare
    *
    * @param coeficientU
    *   coeficient de transmisie termică (W/(m²·K))
    * @param factorSolar
    *   factor solar (g) - fracțiunea din radiația solară care pătrunde prin geam (0-1)
    * @param factorUmbra
    *   factor de umbră (0-1): 1.0 = fără umbră, 0.7 = umbră parțială, 0.0 = umbră totală
    */
  case class Fereastra(
    latime: Length,
    inaltime: Length,
    orientare: Orientare,
    coeficientU: ConductantaTermica,
    factorSolar: Double,
    factorUmbra: Double
  ):
    def suprafata: Area = latime * inaltime

  /** Perete exterior cu caracteristici termice
    *
    * @param coeficientU
    *   coeficient de transmisie termică (W/(m²·K))
    */
  case class PereteExterior(
    latime: Length,
    inaltime: Length,
    orientare: Orientare,
    coeficientU: ConductantaTermica
  ):
    def suprafata: Area = latime * inaltime

  /** Plafon cu caracteristici termice
    *
    * @param coeficientU
    *   coeficient de transmisie termică (W/(m²·K))
    * @param expus
    *   dacă este expus la radiație solară (ultimul etaj, fără izolație)
    */
  case class Plafon(
    suprafata: Area,
    coeficientU: ConductantaTermica,
    expus: Boolean = true
  )

  /** Ocupanți ai spațiului - surse de căldură sensibilă și latentă */
  case class Ocupanti(numarAdulti: Int)

  /** Echipamente electrocasnice - surse de căldură
    *
    * @param factorUtilizare
    *   factor de utilizare simultană (0-1): fracțiunea din puterea instalată care funcționează simultan
    */
  case class Echipamente(
    putereElectrocasnice: Power,
    factorUtilizare: Double
  )

  case class Spatiu(
    nume: String,
    suprafataPardoseala: Area,
    inaltime: Length,
    ferestre: List[Fereastra],
    peretiExterni: List[PereteExterior],
    plafon: Plafon,
    ocupanti: Ocupanti,
    echipamente: Echipamente
  )
