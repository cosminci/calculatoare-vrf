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
VARIANTA: DOAR LIVING

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
    dimensiuni               : 3.3 m × 3.3 m
    A (suprafață)            : 10.889999999999999 m²
    U (coef. transm.)        : 0.35 W/(m²·K)
    ΔT (dif. temp.)          : 10.299999999999997°C

TOTAL Câștiguri termice prin pereți externi: 39.26 W
----------------------------------------------------------------------------------------------------
Câștiguri termice prin ferestre
Formula: Q = Q_transmisie + Q_radiatie(ora 15:00) = Σ(U × A × ΔT) + Σ(A × I × g × F_umbra)
----------------------------------------------------------------------------------------------------
  Fereastră SE: 671.88 W
    dimensiuni               : 4.64 m × 3.3 m
    A (suprafață)            : 15.311999999999998 m²
    U (coef. transm.)        : 1.0 W/(m²·K)
    g (factor solar)         : 0.39
    F_umbra (factor umbră)   : 0.7
    ora critică              : 15:00
    I (radiație totală)      : 123.0 W/m²
    I_D (radiație directă)   : 0.0 W/m²
    I_d (radiație difuză)    : 123.0 W/m²
    ΔT (dif. temp.)          : 10.299999999999997°C
    Q_trans (transmisie)     : 157.71359999999993 W
    Q_rad (radiație)         : 514.1616479999999 W

  Fereastră SV: 896.50 W
    dimensiuni               : 1.05 m × 3.3 m
    A (suprafață)            : 3.465 m²
    U (coef. transm.)        : 1.0 W/(m²·K)
    g (factor solar)         : 0.39
    F_umbra (factor umbră)   : 1.0
    ora critică              : 15:00
    I (radiație totală)      : 637.0 W/m²
    I_D (radiație directă)   : 514.0 W/m²
    I_d (radiație difuză)    : 123.0 W/m²
    ΔT (dif. temp.)          : 10.299999999999997°C
    Q_trans (transmisie)     : 35.68949999999999 W
    Q_rad (radiație)         : 860.80995 W

  Fereastră SV: 2732.19 W
    dimensiuni               : 3.2 m × 3.3 m
    A (suprafață)            : 10.56 m²
    U (coef. transm.)        : 1.0 W/(m²·K)
    g (factor solar)         : 0.39
    F_umbra (factor umbră)   : 1.0
    ora critică              : 15:00
    I (radiație totală)      : 637.0 W/m²
    I_D (radiație directă)   : 514.0 W/m²
    I_d (radiație difuză)    : 123.0 W/m²
    ΔT (dif. temp.)          : 10.299999999999997°C
    Q_trans (transmisie)     : 108.76799999999997 W
    Q_rad (radiație)         : 2623.4208000000003 W

TOTAL Câștiguri termice prin ferestre: 4300.56 W
----------------------------------------------------------------------------------------------------
Câștiguri termice prin plafon
Formula: Q = U × A × ΔT_echivalent
----------------------------------------------------------------------------------------------------
  Plafon expus: 1867.62 W
    A (suprafață)            : 33.5 m²
    U (coef. transm.)        : 2.5 W/(m²·K)
    ΔT_bază (dif. temp.)     : 10.299999999999997°C
    ΔT_rad (radiație sol.)   : 12.0°C
    ΔT_ef (efectiv)          : 22.299999999999997°C

TOTAL Câștiguri termice prin plafon: 1867.62 W
----------------------------------------------------------------------------------------------------
Câștiguri termice de la persoane
Formula: Q = n_adulti × q_adult + n_copii × q_copil
----------------------------------------------------------------------------------------------------
  Adulți: 250.00 W
    n (număr persoane)       : 2
    Q_sens (căldură sens.)   : 150.0 W
    Q_lat (căldură lat.)     : 100.0 W

TOTAL Câștiguri termice de la persoane: 250.00 W
----------------------------------------------------------------------------------------------------
Câștiguri termice de la echipamente electrocasnice
Formula: Q = P_electrocasnice × f_utilizare
----------------------------------------------------------------------------------------------------
  Echipamente electrocasnice: 0.00 W
    P (putere instalată)     : 0.0 W
    f (factor utilizare)     : 0.0

TOTAL Câștiguri termice de la echipamente electrocasnice: 0.00 W
----------------------------------------------------------------------------------------------------
Câștiguri termice prin ventilație/infiltrații
Formula: Q = ṁ × c_p × ΔT = (ρ × V × n / 3600) × c_p × ΔT
----------------------------------------------------------------------------------------------------
  Ventilație/infiltrații: 114.44 W
    V (volum)                : 110.55 m³
    n (schimburi aer)        : 0.3 h⁻¹
    ρ (densitate aer)        : 1.2 kg/m³
    c_p (căldură spec.)      : 1005.0 J/(kg·K)
    ṁ (debit masic)          : 0.01106 kg/s
    ΔT                       : 10.299999999999997°C

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
    dimensiuni               : 3.3 m × 3.3 m
    A (suprafață)            : 10.889999999999999 m²
    U (coef. transm.)        : 0.35 W/(m²·K)
    ΔT (dif. temp.)          : 10.299999999999997°C

  Perete SV: 8.33 W
    dimensiuni               : 0.7 m × 3.3 m
    A (suprafață)            : 2.3099999999999996 m²
    U (coef. transm.)        : 0.35 W/(m²·K)
    ΔT (dif. temp.)          : 10.299999999999997°C

TOTAL Câștiguri termice prin pereți externi: 47.59 W
----------------------------------------------------------------------------------------------------
Câștiguri termice prin ferestre
Formula: Q = Q_transmisie + Q_radiatie(ora 15:00) = Σ(U × A × ΔT) + Σ(A × I × g × F_umbra)
----------------------------------------------------------------------------------------------------
  Fereastră SE: 671.88 W
    dimensiuni               : 4.64 m × 3.3 m
    A (suprafață)            : 15.311999999999998 m²
    U (coef. transm.)        : 1.0 W/(m²·K)
    g (factor solar)         : 0.39
    F_umbra (factor umbră)   : 0.7
    ora critică              : 15:00
    I (radiație totală)      : 123.0 W/m²
    I_D (radiație directă)   : 0.0 W/m²
    I_d (radiație difuză)    : 123.0 W/m²
    ΔT (dif. temp.)          : 10.299999999999997°C
    Q_trans (transmisie)     : 157.71359999999993 W
    Q_rad (radiație)         : 514.1616479999999 W

  Fereastră SV: 896.50 W
    dimensiuni               : 1.05 m × 3.3 m
    A (suprafață)            : 3.465 m²
    U (coef. transm.)        : 1.0 W/(m²·K)
    g (factor solar)         : 0.39
    F_umbra (factor umbră)   : 1.0
    ora critică              : 15:00
    I (radiație totală)      : 637.0 W/m²
    I_D (radiație directă)   : 514.0 W/m²
    I_d (radiație difuză)    : 123.0 W/m²
    ΔT (dif. temp.)          : 10.299999999999997°C
    Q_trans (transmisie)     : 35.68949999999999 W
    Q_rad (radiație)         : 860.80995 W

  Fereastră SV: 2732.19 W
    dimensiuni               : 3.2 m × 3.3 m
    A (suprafață)            : 10.56 m²
    U (coef. transm.)        : 1.0 W/(m²·K)
    g (factor solar)         : 0.39
    F_umbra (factor umbră)   : 1.0
    ora critică              : 15:00
    I (radiație totală)      : 637.0 W/m²
    I_D (radiație directă)   : 514.0 W/m²
    I_d (radiație difuză)    : 123.0 W/m²
    ΔT (dif. temp.)          : 10.299999999999997°C
    Q_trans (transmisie)     : 108.76799999999997 W
    Q_rad (radiație)         : 2623.4208000000003 W

  Fereastră SV: 1707.62 W
    dimensiuni               : 2.0 m × 3.3 m
    A (suprafață)            : 6.6 m²
    U (coef. transm.)        : 1.0 W/(m²·K)
    g (factor solar)         : 0.39
    F_umbra (factor umbră)   : 1.0
    ora critică              : 15:00
    I (radiație totală)      : 637.0 W/m²
    I_D (radiație directă)   : 514.0 W/m²
    I_d (radiație difuză)    : 123.0 W/m²
    ΔT (dif. temp.)          : 10.299999999999997°C
    Q_trans (transmisie)     : 67.97999999999998 W
    Q_rad (radiație)         : 1639.638 W

TOTAL Câștiguri termice prin ferestre: 6008.18 W
----------------------------------------------------------------------------------------------------
Câștiguri termice prin plafon
Formula: Q = U × A × ΔT_echivalent
----------------------------------------------------------------------------------------------------
  Plafon expus: 3796.57 W
    A (suprafață)            : 68.1 m²
    U (coef. transm.)        : 2.5 W/(m²·K)
    ΔT_bază (dif. temp.)     : 10.299999999999997°C
    ΔT_rad (radiație sol.)   : 12.0°C
    ΔT_ef (efectiv)          : 22.299999999999997°C

TOTAL Câștiguri termice prin plafon: 3796.57 W
----------------------------------------------------------------------------------------------------
Câștiguri termice de la persoane
Formula: Q = n_adulti × q_adult + n_copii × q_copil
----------------------------------------------------------------------------------------------------
  Adulți: 250.00 W
    n (număr persoane)       : 2
    Q_sens (căldură sens.)   : 150.0 W
    Q_lat (căldură lat.)     : 100.0 W

TOTAL Câștiguri termice de la persoane: 250.00 W
----------------------------------------------------------------------------------------------------
Câștiguri termice de la echipamente electrocasnice
Formula: Q = P_electrocasnice × f_utilizare
----------------------------------------------------------------------------------------------------
  Echipamente electrocasnice: 150.00 W
    P (putere instalată)     : 150.0 W
    f (factor utilizare)     : 1.0

TOTAL Câștiguri termice de la echipamente electrocasnice: 150.00 W
----------------------------------------------------------------------------------------------------
Câștiguri termice prin ventilație/infiltrații
Formula: Q = ṁ × c_p × ΔT = (ρ × V × n / 3600) × c_p × ΔT
----------------------------------------------------------------------------------------------------
  Ventilație/infiltrații: 215.01 W
    V (volum)                : 207.70499999999998 m³
    n (schimburi aer)        : 0.3 h⁻¹
    ρ (densitate aer)        : 1.2 kg/m³
    c_p (căldură spec.)      : 1005.0 J/(kg·K)
    ṁ (debit masic)          : 0.02077 kg/s
    ΔT                       : 10.299999999999997°C

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
  TOTAL CAPACITATE UNITĂȚI INTERNE               :  16.5 kW

UNITATE EXTERNĂ:
  • Hisense 5AMW125U4RTA (42K)                   :  12.5 kW (nominal la 35°C / 27°C interior)

NOTĂ IMPORTANTĂ:
  • Temperatura maximă de 48°C se referă la temperatura aerului aspirat (care intră în compresor)
  • Unitatea montată pe bloc, în soare, are temperatura aerului aspirat cu ~4°C mai mare
    decât temperatura la umbră (datorită radiației solare și reflexiei de la suprafețe)
  • Factor de corecție capacitate (țevi, înălțime): 1.00

CAPACITATE NECESARĂ:
  • Capacitate totală unități interne: 16.50 kW
  • Necesar cu simultaneitate 60% : 9.90 kW
  • Necesar cu simultaneitate 70% : 11.55 kW
  • Necesar cu simultaneitate 80% : 13.20 kW
  • Necesar cu simultaneitate 90% : 14.85 kW

LEGENDĂ:
  • Deficit: Deficit față de necesarul cu simultaneitate (⚠ = problematic când > 0)
  • Temp. aer aspirat = Temp. la umbră + 4°C (efect soare)
  • Capacitate reală = Capacitate din tabel × 1.00 (factor corecție țevi)


====================================================================================================
PUTERE DE RĂCIRE (din manual) - SET POINT INTERIOR: 27.0°C
====================================================================================================
Temp. aer aspirat (°C) │   15.0 │   20.0 │   25.0 │   30.0 │   35.0 │   40.0 │   45.0 │   48.0
----------------------------------------------------------------------------------------------
Putere răcire (kW)     │  13.25 │  13.13 │  13.00 │  12.75 │  12.50 │  11.25 │   9.13 │   8.75
----------------------------------------------------------------------------------------------------


====================================================================================================
PUTERE DE RĂCIRE (din manual) - SET POINT INTERIOR: 22.0°C
====================================================================================================
Temp. aer aspirat (°C) │   15.0 │   20.0 │   25.0 │   30.0 │   35.0 │   40.0 │   45.0 │   48.0
----------------------------------------------------------------------------------------------
Putere răcire (kW)     │  10.25 │  10.63 │  10.75 │  11.13 │  11.25 │  10.00 │   8.38 │   8.00
----------------------------------------------------------------------------------------------------


====================================================================================================
ANALIZĂ CAPACITATE - SET POINT INTERIOR: 27.0°C - SIMULTANEITATE: 60%
====================================================================================================

 Temp. la  │  Temp. aer   │  Capacitate  │    Deficit
  umbră    │   aspirat    │    reală     │ simultaneitate
   (°C)    │     (°C)     │     (kW)     │     60%
-----------+--------------+--------------+---------------
      31   │     35.0     │    12.50     │     -26.3%
      32   │     36.0     │    12.25     │     -23.7%
      33   │     37.0     │    12.00     │     -21.2%
      34   │     38.0     │    11.75     │     -18.7%
      35   │     39.0     │    11.50     │     -16.2%
      36   │     40.0     │    11.25     │     -13.6%
      37   │     41.0     │    10.83     │      -9.3%
      38   │     42.0     │    10.40     │      -5.1%
      39   │     43.0     │     9.98     │      -0.8%
      40   │     44.0     │     9.55     │  ⚠    3.5%
      41   │     45.0     │     9.13     │  ⚠    7.8%
      42   │     46.0     │     9.00     │  ⚠    9.1%
      43   │     47.0     │     8.88     │  ⚠   10.4%
      44   │     48.0     │     8.75     │  ⚠   11.6%
      45   │     49.0     │     8.75     │  ⚠   11.6%
----------------------------------------------------------------------------------------------------


====================================================================================================
ANALIZĂ CAPACITATE - SET POINT INTERIOR: 22.0°C - SIMULTANEITATE: 60%
====================================================================================================

 Temp. la  │  Temp. aer   │  Capacitate  │    Deficit
  umbră    │   aspirat    │    reală     │ simultaneitate
   (°C)    │     (°C)     │     (kW)     │     60%
-----------+--------------+--------------+---------------
      31   │     35.0     │    11.25     │     -13.6%
      32   │     36.0     │    11.00     │     -11.1%
      33   │     37.0     │    10.75     │      -8.6%
      34   │     38.0     │    10.50     │      -6.1%
      35   │     39.0     │    10.25     │      -3.5%
      36   │     40.0     │    10.00     │      -1.0%
      37   │     41.0     │     9.68     │  ⚠    2.3%
      38   │     42.0     │     9.35     │  ⚠    5.6%
      39   │     43.0     │     9.03     │  ⚠    8.8%
      40   │     44.0     │     8.70     │  ⚠   12.1%
      41   │     45.0     │     8.38     │  ⚠   15.4%
      42   │     46.0     │     8.25     │  ⚠   16.7%
      43   │     47.0     │     8.13     │  ⚠   17.9%
      44   │     48.0     │     8.00     │  ⚠   19.2%
      45   │     49.0     │     8.00     │  ⚠   19.2%
----------------------------------------------------------------------------------------------------

====================================================================================================
CONCLUZII - SIMULTANEITATE 60%
====================================================================================================

Prima apariție a deficitului de capacitate:
  • Set point 27.0°C: La 40°C la umbră (44.0°C aer aspirat), deficit de 3.5%
  • Set point 22.0°C: La 37°C la umbră (41.0°C aer aspirat), deficit de 2.3%

NOTĂ: Aceste valori indică temperatura exterioară la umbră de la care sistemul VRF nu mai poate asigura capacitatea necesară.


====================================================================================================
ANALIZĂ CAPACITATE - SET POINT INTERIOR: 27.0°C - SIMULTANEITATE: 70%
====================================================================================================

 Temp. la  │  Temp. aer   │  Capacitate  │    Deficit
  umbră    │   aspirat    │    reală     │ simultaneitate
   (°C)    │     (°C)     │     (kW)     │     70%
-----------+--------------+--------------+---------------
      31   │     35.0     │    12.50     │      -8.2%
      32   │     36.0     │    12.25     │      -6.1%
      33   │     37.0     │    12.00     │      -3.9%
      34   │     38.0     │    11.75     │      -1.7%
      35   │     39.0     │    11.50     │  ⚠    0.4%
      36   │     40.0     │    11.25     │  ⚠    2.6%
      37   │     41.0     │    10.83     │  ⚠    6.3%
      38   │     42.0     │    10.40     │  ⚠   10.0%
      39   │     43.0     │     9.98     │  ⚠   13.6%
      40   │     44.0     │     9.55     │  ⚠   17.3%
      41   │     45.0     │     9.13     │  ⚠   21.0%
      42   │     46.0     │     9.00     │  ⚠   22.1%
      43   │     47.0     │     8.88     │  ⚠   23.2%
      44   │     48.0     │     8.75     │  ⚠   24.2%
      45   │     49.0     │     8.75     │  ⚠   24.2%
----------------------------------------------------------------------------------------------------


====================================================================================================
ANALIZĂ CAPACITATE - SET POINT INTERIOR: 22.0°C - SIMULTANEITATE: 70%
====================================================================================================

 Temp. la  │  Temp. aer   │  Capacitate  │    Deficit
  umbră    │   aspirat    │    reală     │ simultaneitate
   (°C)    │     (°C)     │     (kW)     │     70%
-----------+--------------+--------------+---------------
      31   │     35.0     │    11.25     │  ⚠    2.6%
      32   │     36.0     │    11.00     │  ⚠    4.8%
      33   │     37.0     │    10.75     │  ⚠    6.9%
      34   │     38.0     │    10.50     │  ⚠    9.1%
      35   │     39.0     │    10.25     │  ⚠   11.3%
      36   │     40.0     │    10.00     │  ⚠   13.4%
      37   │     41.0     │     9.68     │  ⚠   16.2%
      38   │     42.0     │     9.35     │  ⚠   19.0%
      39   │     43.0     │     9.03     │  ⚠   21.9%
      40   │     44.0     │     8.70     │  ⚠   24.7%
      41   │     45.0     │     8.38     │  ⚠   27.5%
      42   │     46.0     │     8.25     │  ⚠   28.6%
      43   │     47.0     │     8.13     │  ⚠   29.7%
      44   │     48.0     │     8.00     │  ⚠   30.7%
      45   │     49.0     │     8.00     │  ⚠   30.7%
----------------------------------------------------------------------------------------------------

====================================================================================================
CONCLUZII - SIMULTANEITATE 70%
====================================================================================================

Prima apariție a deficitului de capacitate:
  • Set point 27.0°C: La 35°C la umbră (39.0°C aer aspirat), deficit de 0.4%
  • Set point 22.0°C: La 31°C la umbră (35.0°C aer aspirat), deficit de 2.6%

NOTĂ: Aceste valori indică temperatura exterioară la umbră de la care sistemul VRF nu mai poate asigura capacitatea necesară.


====================================================================================================
ANALIZĂ CAPACITATE - SET POINT INTERIOR: 27.0°C - SIMULTANEITATE: 80%
====================================================================================================

 Temp. la  │  Temp. aer   │  Capacitate  │    Deficit
  umbră    │   aspirat    │    reală     │ simultaneitate
   (°C)    │     (°C)     │     (kW)     │     80%
-----------+--------------+--------------+---------------
      31   │     35.0     │    12.50     │  ⚠    5.3%
      32   │     36.0     │    12.25     │  ⚠    7.2%
      33   │     37.0     │    12.00     │  ⚠    9.1%
      34   │     38.0     │    11.75     │  ⚠   11.0%
      35   │     39.0     │    11.50     │  ⚠   12.9%
      36   │     40.0     │    11.25     │  ⚠   14.8%
      37   │     41.0     │    10.83     │  ⚠   18.0%
      38   │     42.0     │    10.40     │  ⚠   21.2%
      39   │     43.0     │     9.98     │  ⚠   24.4%
      40   │     44.0     │     9.55     │  ⚠   27.7%
      41   │     45.0     │     9.13     │  ⚠   30.9%
      42   │     46.0     │     9.00     │  ⚠   31.8%
      43   │     47.0     │     8.88     │  ⚠   32.8%
      44   │     48.0     │     8.75     │  ⚠   33.7%
      45   │     49.0     │     8.75     │  ⚠   33.7%
----------------------------------------------------------------------------------------------------


====================================================================================================
ANALIZĂ CAPACITATE - SET POINT INTERIOR: 22.0°C - SIMULTANEITATE: 80%
====================================================================================================

 Temp. la  │  Temp. aer   │  Capacitate  │    Deficit
  umbră    │   aspirat    │    reală     │ simultaneitate
   (°C)    │     (°C)     │     (kW)     │     80%
-----------+--------------+--------------+---------------
      31   │     35.0     │    11.25     │  ⚠   14.8%
      32   │     36.0     │    11.00     │  ⚠   16.7%
      33   │     37.0     │    10.75     │  ⚠   18.6%
      34   │     38.0     │    10.50     │  ⚠   20.5%
      35   │     39.0     │    10.25     │  ⚠   22.3%
      36   │     40.0     │    10.00     │  ⚠   24.2%
      37   │     41.0     │     9.68     │  ⚠   26.7%
      38   │     42.0     │     9.35     │  ⚠   29.2%
      39   │     43.0     │     9.03     │  ⚠   31.6%
      40   │     44.0     │     8.70     │  ⚠   34.1%
      41   │     45.0     │     8.38     │  ⚠   36.6%
      42   │     46.0     │     8.25     │  ⚠   37.5%
      43   │     47.0     │     8.13     │  ⚠   38.4%
      44   │     48.0     │     8.00     │  ⚠   39.4%
      45   │     49.0     │     8.00     │  ⚠   39.4%
----------------------------------------------------------------------------------------------------

====================================================================================================
CONCLUZII - SIMULTANEITATE 80%
====================================================================================================

Prima apariție a deficitului de capacitate:
  • Set point 27.0°C: La 31°C la umbră (35.0°C aer aspirat), deficit de 5.3%
  • Set point 22.0°C: La 31°C la umbră (35.0°C aer aspirat), deficit de 14.8%

NOTĂ: Aceste valori indică temperatura exterioară la umbră de la care sistemul VRF nu mai poate asigura capacitatea necesară.


====================================================================================================
ANALIZĂ CAPACITATE - SET POINT INTERIOR: 27.0°C - SIMULTANEITATE: 90%
====================================================================================================

 Temp. la  │  Temp. aer   │  Capacitate  │    Deficit
  umbră    │   aspirat    │    reală     │ simultaneitate
   (°C)    │     (°C)     │     (kW)     │     90%
-----------+--------------+--------------+---------------
      31   │     35.0     │    12.50     │  ⚠   15.8%
      32   │     36.0     │    12.25     │  ⚠   17.5%
      33   │     37.0     │    12.00     │  ⚠   19.2%
      34   │     38.0     │    11.75     │  ⚠   20.9%
      35   │     39.0     │    11.50     │  ⚠   22.6%
      36   │     40.0     │    11.25     │  ⚠   24.2%
      37   │     41.0     │    10.83     │  ⚠   27.1%
      38   │     42.0     │    10.40     │  ⚠   30.0%
      39   │     43.0     │     9.98     │  ⚠   32.8%
      40   │     44.0     │     9.55     │  ⚠   35.7%
      41   │     45.0     │     9.13     │  ⚠   38.6%
      42   │     46.0     │     9.00     │  ⚠   39.4%
      43   │     47.0     │     8.88     │  ⚠   40.2%
      44   │     48.0     │     8.75     │  ⚠   41.1%
      45   │     49.0     │     8.75     │  ⚠   41.1%
----------------------------------------------------------------------------------------------------


====================================================================================================
ANALIZĂ CAPACITATE - SET POINT INTERIOR: 22.0°C - SIMULTANEITATE: 90%
====================================================================================================

 Temp. la  │  Temp. aer   │  Capacitate  │    Deficit
  umbră    │   aspirat    │    reală     │ simultaneitate
   (°C)    │     (°C)     │     (kW)     │     90%
-----------+--------------+--------------+---------------
      31   │     35.0     │    11.25     │  ⚠   24.2%
      32   │     36.0     │    11.00     │  ⚠   25.9%
      33   │     37.0     │    10.75     │  ⚠   27.6%
      34   │     38.0     │    10.50     │  ⚠   29.3%
      35   │     39.0     │    10.25     │  ⚠   31.0%
      36   │     40.0     │    10.00     │  ⚠   32.7%
      37   │     41.0     │     9.68     │  ⚠   34.8%
      38   │     42.0     │     9.35     │  ⚠   37.0%
      39   │     43.0     │     9.03     │  ⚠   39.2%
      40   │     44.0     │     8.70     │  ⚠   41.4%
      41   │     45.0     │     8.38     │  ⚠   43.6%
      42   │     46.0     │     8.25     │  ⚠   44.4%
      43   │     47.0     │     8.13     │  ⚠   45.3%
      44   │     48.0     │     8.00     │  ⚠   46.1%
      45   │     49.0     │     8.00     │  ⚠   46.1%
----------------------------------------------------------------------------------------------------

====================================================================================================
CONCLUZII - SIMULTANEITATE 90%
====================================================================================================

Prima apariție a deficitului de capacitate:
  • Set point 27.0°C: La 31°C la umbră (35.0°C aer aspirat), deficit de 15.8%
  • Set point 22.0°C: La 31°C la umbră (35.0°C aer aspirat), deficit de 24.2%

NOTĂ: Aceste valori indică temperatura exterioară la umbră de la care sistemul VRF nu mai poate asigura capacitatea necesară.
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
