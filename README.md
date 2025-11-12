# Calculatoare VRF

Calculatoare pentru dimensionarea sistemelor de climatizare VRF și calculul sarcinii termice.

## Instalare Docker

**Windows:**
- Descarcă [Docker Desktop pentru Windows](https://www.docker.com/products/docker-desktop/)
- Rulează installerul și urmează pașii
- Repornește computerul dacă este necesar

**Mac:**
- Descarcă [Docker Desktop pentru Mac](https://www.docker.com/products/docker-desktop/)
- Deschide fișierul `.dmg` și trage Docker în Applications

**Linux:**
```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install docker.io

# Fedora
sudo dnf install docker
```

## Rulare calculatoare

Deschide Terminal (Mac/Linux) sau Command Prompt (Windows) în directorul proiectului și rulează:

**Calculator sarcină termică - Living:**
```bash
docker run --rm -v "$(pwd):/app" -w /app sbtscala/scala-sbt:eclipse-temurin-21.0.8_9_1.11.7_3.3.7 sbt "runMain sarcinaTermica.exemple.OpenSpaceLiving"
```

<details>
<summary>Vezi output exemplu</summary>

```
╔══════════════════════════════════════════════════════════════════════════════════════════════════╗
║                                                                                                  ║
║                    CALCULATOR SARCINĂ TERMICĂ DE RĂCIRE                                          ║
║                         conform I5-2022 și SR 6648-2                                             ║
║                                                                                                  ║
╚══════════════════════════════════════════════════════════════════════════════════════════════════╝

Acest calculator determină sarcina termică de răcire pentru spațiul specificat,
situat la ultimul etaj, cu plafon expus la radiație solară, pentru luna iulie.

Calculul include (conform I5-2022):
  • Câștiguri prin pereți externi (transmisie termică)
  • Câștiguri prin ferestre (transmisie + radiație solară)
  • Câștiguri prin plafon expus (cu supraîncălzire solară)
  • Câștiguri de la persoane (sensibil + latent)
  • Câștiguri de la echipamente electrocasnice (doar cele care funcționează în timpul zilei)
  • Câștiguri prin ventilație/infiltrații

NOTĂ: Iluminatul nu este inclus deoarece nu funcționează în timpul zilei (ora de vârf termic).

Radiația solară este calculată conform I5-2022, Anexa 3, folosind metoda orei critice:
  - Se determină ora la care radiația totală prin toate ferestrele este maximă
  - Se calculează câștigurile folosind valorile simultane la acea oră
  - Radiație totală = I_D (directă) + I_d (difuză)


====================================================================================================
SPAȚIU: Living
Suprafață: 33.5 m²
Înălțime: 3.3 m
Volum: 110.55 m³
====================================================================================================

PARAMETRI CLIMATICI:
  Temperatură exterioară: 35.3°C
  Temperatură interioară: 25.0°C
  Diferență temperatură (ΔT): 10.299999999999997°C
----------------------------------------------------------------------------------------------------
Câștiguri termice prin pereți externi
Formula: Q = Σ(U × A × ΔT)
----------------------------------------------------------------------------------------------------
  Perete SV: 39.26 W

TOTAL Câștiguri termice prin pereți externi: 39.26 W
----------------------------------------------------------------------------------------------------
Câștiguri termice prin ferestre
Formula: Q = Q_transmisie + Q_radiatie(ora 15:00) = Σ(U × A × ΔT) + Σ(A × I × g × F_umbra)
----------------------------------------------------------------------------------------------------
  Fereastră SE: 671.88 W
  Fereastră SV: 896.50 W
  Fereastră SV: 2732.19 W

TOTAL Câștiguri termice prin ferestre: 4300.56 W
----------------------------------------------------------------------------------------------------
Câștiguri termice prin plafon
Formula: Q = U × A × ΔT_echivalent
----------------------------------------------------------------------------------------------------
  Plafon expus: 1867.62 W

TOTAL Câștiguri termice prin plafon: 1867.62 W
----------------------------------------------------------------------------------------------------
Câștiguri termice de la persoane
Formula: Q = n_adulti × q_adult + n_copii × q_copil
----------------------------------------------------------------------------------------------------
  Adulți: 250.00 W

TOTAL Câștiguri termice de la persoane: 250.00 W
----------------------------------------------------------------------------------------------------
Câștiguri termice de la echipamente electrocasnice
Formula: Q = P_electrocasnice × f_utilizare
----------------------------------------------------------------------------------------------------
  Echipamente electrocasnice: 0.00 W

TOTAL Câștiguri termice de la echipamente electrocasnice: 0.00 W
----------------------------------------------------------------------------------------------------
Câștiguri termice prin ventilație/infiltrații
Formula: Q = ṁ × c_p × ΔT = (ρ × V × n / 3600) × c_p × ΔT
----------------------------------------------------------------------------------------------------
  Ventilație/infiltrații: 114.44 W

TOTAL Câștiguri termice prin ventilație/infiltrații: 114.44 W
====================================================================================================
REZUMAT CÂȘTIGURI TERMICE
====================================================================================================
Câștiguri termice prin pereți externi             : 39.26 W
Câștiguri termice prin ferestre                   : 4300.56 W
Câștiguri termice prin plafon                     : 1867.62 W
Câștiguri termice de la persoane                  : 250.00 W
Câștiguri termice de la echipamente electrocasnice: 0.00 W
Câștiguri termice prin ventilație/infiltrații     : 114.44 W
----------------------------------------------------------------------------------------------------
TOTAL CÂȘTIGURI TERMICE                           : 6571.88 W
====================================================================================================

>>> PUTERE NECESARĂ INSTALAȚIE CLIMATIZARE: 6571.88 W <<<

NOTĂ: Se recomandă adăugarea unui coeficient de siguranță de 10-15% pentru dimensionarea instalației.
>>> PUTERE RECOMANDATĂ (cu 15% siguranță): 7557.67 W <<<
```

</details>

**Calculator sarcină termică - Living + Bucătărie:**
```bash
docker run --rm -v "$(pwd):/app" -w /app sbtscala/scala-sbt:eclipse-temurin-21.0.8_9_1.11.7_3.3.7 sbt "runMain sarcinaTermica.exemple.OpenSpaceLivingBucatarie"
```

<details>
<summary>Vezi output exemplu</summary>

```
VARIANTA: LIVING + BUCĂTĂRIE

╔══════════════════════════════════════════════════════════════════════════════════════════════════╗
║                                                                                                  ║
║                    CALCULATOR SARCINĂ TERMICĂ DE RĂCIRE                                          ║
║                         conform I5-2022 și SR 6648-2                                             ║
║                                                                                                  ║
╚══════════════════════════════════════════════════════════════════════════════════════════════════╝

Acest calculator determină sarcina termică de răcire pentru spațiul specificat,
situat la ultimul etaj, cu plafon expus la radiație solară, pentru luna iulie.

Calculul include (conform I5-2022):
  • Câștiguri prin pereți externi (transmisie termică)
  • Câștiguri prin ferestre (transmisie + radiație solară)
  • Câștiguri prin plafon expus (cu supraîncălzire solară)
  • Câștiguri de la persoane (sensibil + latent)
  • Câștiguri de la echipamente electrocasnice (doar cele care funcționează în timpul zilei)
  • Câștiguri prin ventilație/infiltrații

NOTĂ: Iluminatul nu este inclus deoarece nu funcționează în timpul zilei (ora de vârf termic).

Radiația solară este calculată conform I5-2022, Anexa 3, folosind metoda orei critice:
  - Se determină ora la care radiația totală prin toate ferestrele este maximă
  - Se calculează câștigurile folosind valorile simultane la acea oră
  - Radiație totală = I_D (directă) + I_d (difuză)


====================================================================================================
SPAȚIU: Living + Bucătărie
Suprafață: 42.1 m²
Înălțime: 3.3 m
Volum: 138.93 m³
====================================================================================================

PARAMETRI CLIMATICI:
  Temperatură exterioară: 35.3°C
  Temperatură interioară: 25.0°C
  Diferență temperatură (ΔT): 10.299999999999997°C
----------------------------------------------------------------------------------------------------
Câștiguri termice prin pereți externi
Formula: Q = Σ(U × A × ΔT)
----------------------------------------------------------------------------------------------------
  Perete SV: 39.26 W
  Perete SV: 7.07 W

TOTAL Câștiguri termice prin pereți externi: 46.32 W
----------------------------------------------------------------------------------------------------
Câștiguri termice prin ferestre
Formula: Q = Q_transmisie + Q_radiatie(ora 15:00) = Σ(U × A × ΔT) + Σ(A × I × g × F_umbra)
----------------------------------------------------------------------------------------------------
  Fereastră SE: 671.88 W
  Fereastră SV: 896.50 W
  Fereastră SV: 2732.19 W
  Fereastră SV: 1707.62 W

TOTAL Câștiguri termice prin ferestre: 6008.18 W
----------------------------------------------------------------------------------------------------
Câștiguri termice prin plafon
Formula: Q = U × A × ΔT_echivalent
----------------------------------------------------------------------------------------------------
  Plafon expus: 2347.08 W

TOTAL Câștiguri termice prin plafon: 2347.08 W
----------------------------------------------------------------------------------------------------
Câștiguri termice de la persoane
Formula: Q = n_adulti × q_adult + n_copii × q_copil
----------------------------------------------------------------------------------------------------
  Adulți: 250.00 W

TOTAL Câștiguri termice de la persoane: 250.00 W
----------------------------------------------------------------------------------------------------
Câștiguri termice de la echipamente electrocasnice
Formula: Q = P_electrocasnice × f_utilizare
----------------------------------------------------------------------------------------------------
  Echipamente electrocasnice: 150.00 W

TOTAL Câștiguri termice de la echipamente electrocasnice: 150.00 W
----------------------------------------------------------------------------------------------------
Câștiguri termice prin ventilație/infiltrații
Formula: Q = ṁ × c_p × ΔT = (ρ × V × n / 3600) × c_p × ΔT
----------------------------------------------------------------------------------------------------
  Ventilație/infiltrații: 143.81 W

TOTAL Câștiguri termice prin ventilație/infiltrații: 143.81 W
====================================================================================================
REZUMAT CÂȘTIGURI TERMICE
====================================================================================================
Câștiguri termice prin pereți externi             : 46.32 W
Câștiguri termice prin ferestre                   : 6008.18 W
Câștiguri termice prin plafon                     : 2347.08 W
Câștiguri termice de la persoane                  : 250.00 W
Câștiguri termice de la echipamente electrocasnice: 150.00 W
Câștiguri termice prin ventilație/infiltrații     : 143.81 W
----------------------------------------------------------------------------------------------------
TOTAL CÂȘTIGURI TERMICE                           : 8945.39 W
====================================================================================================

>>> PUTERE NECESARĂ INSTALAȚIE CLIMATIZARE: 8945.39 W <<<

NOTĂ: Se recomandă adăugarea unui coeficient de siguranță de 10-15% pentru dimensionarea instalației.
>>> PUTERE RECOMANDATĂ (cu 15% siguranță): 10287.20 W <<<
```

</details>

**Calculator sarcină termică - Open Space Complet:**
```bash
docker run --rm -v "$(pwd):/app" -w /app sbtscala/scala-sbt:eclipse-temurin-21.0.8_9_1.11.7_3.3.7 sbt "runMain sarcinaTermica.exemple.OpenSpaceComplet"
```

<details>
<summary>Vezi output exemplu</summary>

```
VARIANTA: OPEN SPACE COMPLET (Living + Bucătărie + Hol/Scări)

╔══════════════════════════════════════════════════════════════════════════════════════════════════╗
║                                                                                                  ║
║                    CALCULATOR SARCINĂ TERMICĂ DE RĂCIRE                                          ║
║                         conform I5-2022 și SR 6648-2                                             ║
║                                                                                                  ║
╚══════════════════════════════════════════════════════════════════════════════════════════════════╝

Acest calculator determină sarcina termică de răcire pentru spațiul specificat,
situat la ultimul etaj, cu plafon expus la radiație solară, pentru luna iulie.

Calculul include (conform I5-2022):
  • Câștiguri prin pereți externi (transmisie termică)
  • Câștiguri prin ferestre (transmisie + radiație solară)
  • Câștiguri prin plafon expus (cu supraîncălzire solară)
  • Câștiguri de la persoane (sensibil + latent)
  • Câștiguri de la echipamente electrocasnice (doar cele care funcționează în timpul zilei)
  • Câștiguri prin ventilație/infiltrații

NOTĂ: Iluminatul nu este inclus deoarece nu funcționează în timpul zilei (ora de vârf termic).

Radiația solară este calculată conform I5-2022, Anexa 3, folosind metoda orei critice:
  - Se determină ora la care radiația totală prin toate ferestrele este maximă
  - Se calculează câștigurile folosind valorile simultane la acea oră
  - Radiație totală = I_D (directă) + I_d (difuză)


====================================================================================================
SPAȚIU: Open Space Complet (Living + Bucătărie + Hol/Scări)
Suprafață: 68.1 m²
Înălțime: 3.05 m
Volum: 207.70499999999998 m³
====================================================================================================

PARAMETRI CLIMATICI:
  Temperatură exterioară: 35.3°C
  Temperatură interioară: 25.0°C
  Diferență temperatură (ΔT): 10.299999999999997°C
----------------------------------------------------------------------------------------------------
Câștiguri termice prin pereți externi
Formula: Q = Σ(U × A × ΔT)
----------------------------------------------------------------------------------------------------
  Perete SV: 39.26 W
  Perete SV: 8.33 W

TOTAL Câștiguri termice prin pereți externi: 47.59 W
----------------------------------------------------------------------------------------------------
Câștiguri termice prin ferestre
Formula: Q = Q_transmisie + Q_radiatie(ora 15:00) = Σ(U × A × ΔT) + Σ(A × I × g × F_umbra)
----------------------------------------------------------------------------------------------------
  Fereastră SE: 671.88 W
  Fereastră SV: 896.50 W
  Fereastră SV: 2732.19 W
  Fereastră SV: 1707.62 W

TOTAL Câștiguri termice prin ferestre: 6008.18 W
----------------------------------------------------------------------------------------------------
Câștiguri termice prin plafon
Formula: Q = U × A × ΔT_echivalent
----------------------------------------------------------------------------------------------------
  Plafon expus: 3796.57 W

TOTAL Câștiguri termice prin plafon: 3796.57 W
----------------------------------------------------------------------------------------------------
Câștiguri termice de la persoane
Formula: Q = n_adulti × q_adult + n_copii × q_copil
----------------------------------------------------------------------------------------------------
  Adulți: 250.00 W

TOTAL Câștiguri termice de la persoane: 250.00 W
----------------------------------------------------------------------------------------------------
Câștiguri termice de la echipamente electrocasnice
Formula: Q = P_electrocasnice × f_utilizare
----------------------------------------------------------------------------------------------------
  Echipamente electrocasnice: 150.00 W

TOTAL Câștiguri termice de la echipamente electrocasnice: 150.00 W
----------------------------------------------------------------------------------------------------
Câștiguri termice prin ventilație/infiltrații
Formula: Q = ṁ × c_p × ΔT = (ρ × V × n / 3600) × c_p × ΔT
----------------------------------------------------------------------------------------------------
  Ventilație/infiltrații: 215.01 W

TOTAL Câștiguri termice prin ventilație/infiltrații: 215.01 W
====================================================================================================
REZUMAT CÂȘTIGURI TERMICE
====================================================================================================
Câștiguri termice prin pereți externi             : 47.59 W
Câștiguri termice prin ferestre                   : 6008.18 W
Câștiguri termice prin plafon                     : 3796.57 W
Câștiguri termice de la persoane                  : 250.00 W
Câștiguri termice de la echipamente electrocasnice: 150.00 W
Câștiguri termice prin ventilație/infiltrații     : 215.01 W
----------------------------------------------------------------------------------------------------
TOTAL CÂȘTIGURI TERMICE                           : 10467.35 W
====================================================================================================

>>> PUTERE NECESARĂ INSTALAȚIE CLIMATIZARE: 10467.35 W <<<

NOTĂ: Se recomandă adăugarea unui coeficient de siguranță de 10-15% pentru dimensionarea instalației.
>>> PUTERE RECOMANDATĂ (cu 15% siguranță): 12037.45 W <<<
```

</details>

**Analiză sistem VRF Hisense:**
```bash
docker run --rm -v "$(pwd):/app" -w /app sbtscala/scala-sbt:eclipse-temurin-21.0.8_9_1.11.7_3.3.7 sbt "runMain vrf.exemple.ExempluHisense"
```

<details>
<summary>Vezi output exemplu</summary>

```
====================================================================================================
ANALIZA DIMENSIONARE SISTEM VRF
====================================================================================================

SURSĂ DATE: Hisense/Cirelius Technical & Service Manual V5.0 - Multi-Split Type Air Conditioners (R32)

UNITĂȚI INTERNE INSTALATE:
  • Hisense ADT71UX4RCL4 (Open Space)            :   7.2 kW
  • Hisense ADT35UX4RBL4 (Dormitor 1)            :   3.5 kW
  • Hisense ADT26UX4RBL4 (Dormitor 2)            :   2.9 kW
  • Hisense ADT26UX4RBL4 (Birou)                 :   2.9 kW
  ------------------------------------------------------------
  TOTAL CAPACITATE UNITĂȚI INTERNE:                 16.5 kW

UNITATE EXTERNĂ:
  • Hisense 5AMW125U4RTA (42K)                   :  12.5 kW (nominal la 35°C / 27°C interior)

NOTĂ IMPORTANTĂ:
  • Temperatura maximă de 48°C se referă la temperatura aerului aspirat (care intră în compresor)
  • Unitatea montată pe bloc, în soare, are temperatura aerului aspirat cu ~4°C mai mare
    decât temperatura la umbră (datorită radiației solare și reflexiei de la suprafețe)
  • Factor de corecție capacitate (țevi, înălțime): 1.00


====================================================================================================
ANALIZA CAPACITATE - SET POINT INTERIOR: 22.0°C
====================================================================================================

CAPACITATE NECESARĂ:
  • Capacitate totală unități interne: 16.50 kW
  • Necesar cu simultaneitate 75%: 12.38 kW
  • Necesar cu simultaneitate 100%: 16.50 kW

LEGENDĂ:
  • Deficit 75%: Deficit față de necesarul cu simultaneitate 75% (⚠ = problematic)
  • Deficit 100%: Deficit față de necesarul cu simultaneitate 100% (⚠ = problematic când > 25%)
  • Temp. aer aspirat = Temp. la umbră + 4°C (efect soare)
  • Capacitate reală = Capacitate din tabel × 1.00 (factor corecție țevi)
----------------------------------------------------------------------------------------------------
 Temp. la  │  Temp. aer   │  Capacitate  │    Deficit     │    Deficit
  umbră    │   aspirat    │    reală     │ simultaneitate │ simultaneitate
   (°C)    │     (°C)     │     (kW)     │      75%       │      100%
-----------+--------------+--------------+----------------+---------------
      31   │     35.0     │    11.25     │  ⚠    9.1%     │  ⚠   31.8%
      32   │     36.0     │    11.00     │  ⚠   11.1%     │  ⚠   33.3%
      33   │     37.0     │    10.75     │  ⚠   13.1%     │  ⚠   34.8%
      34   │     38.0     │    10.50     │  ⚠   15.2%     │  ⚠   36.4%
      35   │     39.0     │    10.25     │  ⚠   17.2%     │  ⚠   37.9%
      36   │     40.0     │    10.00     │  ⚠   19.2%     │  ⚠   39.4%
      37   │     41.0     │     9.68     │  ⚠   21.8%     │  ⚠   41.4%
      38   │     42.0     │     9.35     │  ⚠   24.4%     │  ⚠   43.3%
      39   │     43.0     │     9.03     │  ⚠   27.1%     │  ⚠   45.3%
      40   │     44.0     │     8.70     │  ⚠   29.7%     │  ⚠   47.3%
      41   │     45.0     │     8.38     │  ⚠   32.3%     │  ⚠   49.2%
      42   │     46.0     │     8.25     │  ⚠   33.3%     │  ⚠   50.0%
      43   │     47.0     │     8.13     │  ⚠   34.3%     │  ⚠   50.8%
      44   │     48.0     │     8.00     │  ⚠   35.4%     │  ⚠   51.5%
      45   │     49.0     │     8.00     │  ⚠   35.4%     │  ⚠   51.5%
----------------------------------------------------------------------------------------------------


====================================================================================================
ANALIZA CAPACITATE - SET POINT INTERIOR: 27.0°C
====================================================================================================

CAPACITATE NECESARĂ:
  • Capacitate totală unități interne: 16.50 kW
  • Necesar cu simultaneitate 75%: 12.38 kW
  • Necesar cu simultaneitate 100%: 16.50 kW

LEGENDĂ:
  • Deficit 75%: Deficit față de necesarul cu simultaneitate 75% (⚠ = problematic)
  • Deficit 100%: Deficit față de necesarul cu simultaneitate 100% (⚠ = problematic când > 25%)
  • Temp. aer aspirat = Temp. la umbră + 4°C (efect soare)
  • Capacitate reală = Capacitate din tabel × 1.00 (factor corecție țevi)
----------------------------------------------------------------------------------------------------
 Temp. la  │  Temp. aer   │  Capacitate  │    Deficit     │    Deficit
  umbră    │   aspirat    │    reală     │ simultaneitate │ simultaneitate
   (°C)    │     (°C)     │     (kW)     │      75%       │      100%
-----------+--------------+--------------+----------------+---------------
      31   │     35.0     │    12.50     │      -1.0%     │      24.2%
      32   │     36.0     │    12.25     │  ⚠    1.0%     │  ⚠   25.8%
      33   │     37.0     │    12.00     │  ⚠    3.0%     │  ⚠   27.3%
      34   │     38.0     │    11.75     │  ⚠    5.1%     │  ⚠   28.8%
      35   │     39.0     │    11.50     │  ⚠    7.1%     │  ⚠   30.3%
      36   │     40.0     │    11.25     │  ⚠    9.1%     │  ⚠   31.8%
      37   │     41.0     │    10.83     │  ⚠   12.5%     │  ⚠   34.4%
      38   │     42.0     │    10.40     │  ⚠   16.0%     │  ⚠   37.0%
      39   │     43.0     │     9.98     │  ⚠   19.4%     │  ⚠   39.5%
      40   │     44.0     │     9.55     │  ⚠   22.8%     │  ⚠   42.1%
      41   │     45.0     │     9.13     │  ⚠   26.3%     │  ⚠   44.7%
      42   │     46.0     │     9.00     │  ⚠   27.3%     │  ⚠   45.5%
      43   │     47.0     │     8.88     │  ⚠   28.3%     │  ⚠   46.2%
      44   │     48.0     │     8.75     │  ⚠   29.3%     │  ⚠   47.0%
      45   │     49.0     │     8.75     │  ⚠   29.3%     │  ⚠   47.0%
----------------------------------------------------------------------------------------------------
```

</details>

**Pe Windows (Command Prompt)**, înlocuiește `$(pwd)` cu `%cd%`:
```cmd
docker run --rm -v "%cd%:/app" -w /app sbtscala/scala-sbt:eclipse-temurin-21.0.8_9_1.11.7_3.3.7 sbt "runMain sarcinaTermica.exemple.OpenSpaceLiving"
```

**Pe Windows (PowerShell)**, înlocuiește `$(pwd)` cu `${PWD}`:
```powershell
docker run --rm -v "${PWD}:/app" -w /app sbtscala/scala-sbt:eclipse-temurin-21.0.8_9_1.11.7_3.3.7 sbt "runMain sarcinaTermica.exemple.OpenSpaceLiving"
```

### Notă
Prima rulare va dura mai mult (câteva minute) pentru că Docker descarcă imaginea și compilează proiectul. Rulările următoare vor fi mult mai rapide.

## Ce calculează fiecare exemplu?

- **OpenSpaceLiving** - Calculează sarcina termică doar pentru un  living
- **OpenSpaceLivingBucatarie** - Calculează sarcina termică pentru living + bucătărie
- **OpenSpaceComplet** - Calculează sarcina termică pentru living + bucătărie + hol/scări
- **ExempluHisense** - Analizează dimensionarea sistemului VRF Hisense (verifică dacă unitatea externă are capacitate suficientă pentru unitățile interne)
