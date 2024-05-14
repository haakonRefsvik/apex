# Apex

## Group members:

    Magnus Kleven
    Julian Rubilar
    Nasteeho Abdullahi Elmi
    Jostein Jensen
    Suad Raage
    Håkon Refsvik
    

# Where to find more documentation:

## ARCHITECTURE.MD:
   // Here you can find the 
        

# How to run the app:

## IFI-Proxy:

    The API key for the project, which we use to retrive weather data, is not included. This is due to security reasons.

    The API key is placed in:
        res -> values -> strings.xml 

    in the format:
        <string name="in2000ProxyKey" translatable="false">YOUR-API-KEY-HERE</string>

## Mapbox:

    If the app is not running due to lack of keys or fobidden authorization, try this:

### Public key:

    Go to:
        -> Gradle Scripts -> local.properties

    Then find the variable:
        -> MAPBOX_DOWNLOADS_TOKEN = PRIVATE KEY

    It should be named MAPBOX_DOWNLOADS_TOKEN, or have a similiar variable name as in:
        -> settings.gradle.kt 
            -> localProperties.getProperty("Variabel)


### Private key:
    The public key should be placed in:
        -> res -> values -> strings.xml

    Under resources like this:  
        <string name="mapbox_access_token" translatable="false" tools:ignore="UnusedResources">PUBLIC API KEY</string>.


### settings.gradle.kt:

    This should be in settings.gradle.kts: 

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
        
# Libraries:

### Vico by Patryk Goworowski:
    - Library for showing Graphs
    
### Unidata netcdf-java:
    - Library for parsing Grib-files
    
### Mapbox:
    - For displaying map
    
### Room:
    - Databases
