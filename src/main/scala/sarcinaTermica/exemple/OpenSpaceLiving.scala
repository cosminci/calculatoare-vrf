package sarcinaTermica.exemple

import squants.thermal.*
import squants.thermal.TemperatureConversions.given
import squants.space.*
import squants.space.LengthConversions.given
import squants.space.AreaConversions.given
import squants.energy.*
import squants.energy.PowerConversions.given
import sarcinaTermica.*

object OpenSpaceLiving:

  /** Parametri climatici pentru București, iulie Conform I5-2022, Anexa 1: București - Temperatură = 35.3°C */
  val parametriClimatici = ParametriClimatici(
    temperaturaExterioara = 35.3.celsius,
    temperaturaInterioara = 25.0.celsius
  )

  // Caracteristici ferestre - Saint Gobain COOL-LITE® KN 166 II
  private val CoeficientUFerestre: ConductantaTermica = 1.0  // W/(m²·K)
  private val FactorSolarFerestre                     = 0.39 // g

  private val CoeficientUPereti: ConductantaTermica = 0.35 // W/(m²·K) - perete izolat conform normativ
  private val CoeficientUPlafon: ConductantaTermica = 2.5  // W/(m²·K) - plafon neizolat, expus la soare

  private val InaltimeLiving = 3.3.meters

  private object Ferestre:

    def fereastraSE: Fereastra = Fereastra(
      latime = 4.64.meters,
      inaltime = InaltimeLiving,
      orientare = Orientare.SE,
      coeficientU = CoeficientUFerestre,
      factorSolar = FactorSolarFerestre,
      factorUmbra = 0.7 // terasă acoperită de 1.8m lățime oferă umbră parțială
    )

    def fereastraSV1: Fereastra = Fereastra(
      latime = 1.05.meters,
      inaltime = InaltimeLiving,
      orientare = Orientare.SV,
      coeficientU = CoeficientUFerestre,
      factorSolar = FactorSolarFerestre,
      factorUmbra = 1.0 // fără umbră
    )

    def fereastraSV2: Fereastra = Fereastra(
      latime = 3.20.meters,
      inaltime = InaltimeLiving,
      orientare = Orientare.SV,
      coeficientU = CoeficientUFerestre,
      factorSolar = FactorSolarFerestre,
      factorUmbra = 1.0 // fără umbră
    )

  private val spatiuLiving: Spatiu =
    val suprafata = 33.5.squareMeters
    val peretiExterni = List(
      PereteExterior(
        latime = 3.3.meters,
        inaltime = InaltimeLiving,
        orientare = Orientare.SV,
        coeficientU = CoeficientUPereti
      )
    )

    val plafon   = Plafon(suprafata, CoeficientUPlafon)
    val ocupanti = Ocupanti(numarAdulti = 2)
    val echipamente = Echipamente(
      putereElectrocasnice = 0.watts,
      factorUtilizare = 0.0
    )

    Spatiu(
      nume = "Living",
      suprafataPardoseala = suprafata,
      inaltime = InaltimeLiving,
      ferestre = List(Ferestre.fereastraSE, Ferestre.fereastraSV1, Ferestre.fereastraSV2),
      peretiExterni = peretiExterni,
      plafon = plafon,
      ocupanti = ocupanti,
      echipamente = echipamente
    )

  def main(args: Array[String]): Unit =
    println("VARIANTA: DOAR LIVING\n")
    val calculator = CalculatorSarcinaTermica(spatiuLiving, parametriClimatici)
    val rezultat   = calculator.calculeaza
    println(calculator.genereazaRaport(rezultat))
