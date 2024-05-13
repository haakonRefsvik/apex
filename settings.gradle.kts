import java.util.Properties
import java.io.File

val localProperties = Properties()
localProperties.load(File("local.properties").inputStream())


pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://artifacts.unidata.ucar.edu/repository/unidata-all/") }
    }
}

dependencyResolutionManagement {
    //repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
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

rootProject.name = "rakettoppskytning"
include(":app")
 