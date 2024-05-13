# team-25

<<<<<<< HEAD
=======

## Mapbox:

    Hvis appen ikke kjører på grunn av api nøklene mangler, så skal den offentlige nøkkelen ligge i  res -> values -> strings.xml
    under resources på denne måten:  <string name="mapbox_access_token" translatable="false" tools:ignore="UnusedResources">OFFENTLIG APINØKKEL</string>.
    Hvis api nøkkel for å laste ned kartet ikke fungrer så skal den ligge i Gradle Scripts -> local.properties på denne måten: MAPBOX_DOWNLOADS_TOKEN = PRIVAT APINØKKEL
    
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