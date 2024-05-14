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
    2. Open the repository in Android Studio
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


#### settings.gradle.kt:

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
    Vico is a graph visualization library designed by Patryk Goworowski. It provides a comprehensive set of tools for creating and displaying various types of graphs.
    -> Link to website: https://patrykandpatrick.com/vico/wiki/
    -> 
    
## Unidata netcdf-java by Thredds:
    Unidata netcdf-java, developed by Thredds, is a powerful library for parsing GRIB files. It offers extensive functionality for handling meteorological and oceanographic data in the GRIB format.
    -> Link to website: https://docs.unidata.ucar.edu/netcdf-java/current/userguide/grib_files_cdm.html
    
## Mapbox:
    Mapbox is a versatile mapping platform that offers tools and services for displaying maps in web and mobile applications. It provides APIs and SDKs for integrating interactive maps with various functionalities.
    -> Link to website: https://docs.mapbox.com/android/maps/guides/
    
## Room:
    Room is an Android library provided by Google for building local SQLite databases and abstracting away some of the complexities of working with SQLite directly. It simplifies the process of storing and accessing structured data locally within Android apps.
    -> Link to website: https://developer.android.com/training/data-storage/room/
