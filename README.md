# Apex

# Mapbox:

    Hvis appen ikke kjører på grunnet mangel av nøkler eller forbbiden authorisation, 
    så prøv dette:

### Privalt nøkel:

    Den offentlige nøkkelen burde ligge i 
    res -> values -> strings.xml
    Variablen burde hete MAPBOX_DOWNLOADS_TOKEN,
    eller endre til det samme som er settings.gradle.kt under getProperty

### Offentlig nøkel:

    Under resources på denne måten:  <string name="mapbox_access_token" translatable="false" 
    tools:ignore="UnusedResources">OFFENTLIG APINØKKEL</string>.
    Hvis api nøkkel for å laste ned kartet ikke fungrer så skal den ligge i 
    Gradle Scripts -> local.properties på denne måten: MAPBOX_DOWNLOADS_TOKEN = PRIVAT APINØKKEL

### settings.gradle.kt:

    Dette må ligge i settings.gradle.kts: 
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