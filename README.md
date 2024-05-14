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
    This file is intedned for readers who will work with the opreation, maintanance and further development of the solution. 
    It explains the technologies and archiitecture used in the application. It also describes API level the aplication is intended for

## README.MD:
    This file guides users on how use the app for the first time in case of an API-key error occures.
    It also explains the different libraries used.

## The code itself:
    The code is well documented and commented. Each function or file explains what it does. 

# How to run the app:
    1. Clone this Github repository
    2. Open the REpository in ANdroid Studio
    3. The recommended emulator is Resizable Experimental 33/34, since that is the emulator the code has been tested on.
    4. Follow the steps under IFI-Proxy and Mapbox to fix error caused by missing keys
    
    
## IFI-Proxy:

    The API key for the project, which we use to retrive weather data, is not included. This is due to security reasons.

    The API key is placed in:
        res -> values -> strings.xml 

    in the format:
        <string name="in2000ProxyKey" translatable="false">YOUR-API-KEY-HERE</string>

## Mapbox:

    If the app is not running due to lack of keys or fobidden authorization, try this:

#### Public key:

    Go to:
        -> Gradle Scripts -> local.properties

    Then find the variable:
        -> MAPBOX_DOWNLOADS_TOKEN = PRIVATE KEY

    It should be named MAPBOX_DOWNLOADS_TOKEN, or have a similiar variable name as in:
        -> settings.gradle.kt 
            -> localProperties.getProperty("Variabel)


#### Private key:
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

## Vico by Patryk Goworowski:
    -> Library for showing Graphs
    
## Unidata netcdf-java by Thredds:
    -> Library for parsing Grib-files
    
## Mapbox:
    -> For displaying map
    
## Room:
    -> For storing in local database
