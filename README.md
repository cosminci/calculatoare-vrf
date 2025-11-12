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

**Calculator sarcină termică - Living + Bucătărie:**
```bash
docker run --rm -v "$(pwd):/app" -w /app sbtscala/scala-sbt:eclipse-temurin-21.0.8_9_1.11.7_3.3.7 sbt "runMain sarcinaTermica.exemple.OpenSpaceLivingBucatarie"
```

**Calculator sarcină termică - Open Space Complet:**
```bash
docker run --rm -v "$(pwd):/app" -w /app sbtscala/scala-sbt:eclipse-temurin-21.0.8_9_1.11.7_3.3.7 sbt "runMain sarcinaTermica.exemple.OpenSpaceComplet"
```

**Analiză sistem VRF Hisense:**
```bash
docker run --rm -v "$(pwd):/app" -w /app sbtscala/scala-sbt:eclipse-temurin-21.0.8_9_1.11.7_3.3.7 sbt "runMain vrf.exemple.ExempluHisense"
```

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
