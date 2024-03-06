
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://artifacts.unidata.ucar.edu/repository/unidata-all/") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://artifacts.unidata.ucar.edu/repository/unidata-all/") }

    }
}

rootProject.name = "rakettoppskytning"
include(":app")
 