# Apex

## Gruppemedlemmer:

    Magnus Kleven
    Julian Rubilar
    Nasteeho Abdullahi Elmi
    Jostein Jensen
    Suad Raage
    Håkon Refsvik
    
# IFI-Proxy:

    Det følger ikke med en API-nøkkel til prosjektet som vi bruker for å hente værdatas. 
    Dette har vi gjort av sikkerhetsmessige årsaker. 
    API-nøkkelen skal ligge i 
    res -> values -> strings.xml 
    på formatet
    <string name="in2000ProxyKey" translatable="false">DIN-API-NØKKEL-HER</string>

# Mapbox:

    Hvis appen ikke kjører grunnet mangel av nøkler eller forbbiden authorisation, 
    så prøv dette:

### Offentlig nøkkel:

  
    Gradle Scripts -> local.properties på denne måten: MAPBOX_DOWNLOADS_TOKEN = PRIVAT APINØKKEL
    Variablen burde hete MAPBOX_DOWNLOADS_TOKEN,
    eller ha et likt variabel navn som i settings.gradle.kt localProperties.getProperty("Variabel)
    

### Privat nøkkel:
    Den offentlige nøkkelen burde ligge i res -> values -> strings.xml
    Under resources på denne måten:  
    <string name="mapbox_access_token" translatable="false" tools:ignore="UnusedResources">OFFENTLIG APINØKKEL</string>.
   

### settings.gradle.kt:

    Dette burde ligge i settings.gradle.kts: 
    dependencyResolutionManagement {
        repositories {
            google()
            mavenCentral()
            maven { url = uri("https://artifacts.unidata.ucar.edu/repository/unidata-all/") }
            maven {
                url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
    
                credentials.username = "mapbox"
                credentials.password = localProperties.getProperty("MAPBOX_DOWNLOADS_TOKEN")
                authentication.create<BasicAuthentication>("basic")
            }
    
        }
    }
