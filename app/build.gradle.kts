import groovy.util.logging.Slf4j
import org.jetbrains.kotlin.gradle.plugin.extraProperties
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
}
android {
    namespace = "no.uio.ifi.in2000.rakettoppskytning"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // Please ensure you have a valid API KEY for themoviedb.org↵
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {

        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            pickFirsts += "META-INF/DEPENDENCIES"
        }
    }


}

dependencies {
    implementation("androidx.media3:media3-common:1.2.1")
    implementation("androidx.test:core-ktx:1.5.0")
    implementation("androidx.media3:media3-test-utils:1.3.1")
    val ktorVersion = "2.3.8"
    val navVersion = "2.7.7"
    val kotlinVersion = "1.9.22"
    val material3Version = "3:1.2.0"
    val netcdfJavaVersion = "5.5.2"
    val slf4jVersion = "1.7.30"

    androidTestImplementation("androidx.test:core-ktx:1.4.0")

    // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")

    // JUnit4 Framework
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.3")

    // Truth Assertions
    androidTestImplementation("androidx.test.ext:truth:1.4.0")
    androidTestImplementation("com.google.truth:truth:1.1.3")

    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")


    implementation("com.patrykandpatrick.vico:compose:2.0.0-alpha.14")
    implementation("com.patrykandpatrick.vico:compose-m2:2.0.0-alpha.14")
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-alpha.14")
    implementation("com.patrykandpatrick.vico:core:2.0.0-alpha.14")
    implementation("com.patrykandpatrick.vico:views:2.0.0-alpha.14")

    implementation ("com.google.code.gson:gson:2.8.7")

    implementation("edu.ucar:cdm-core:${netcdfJavaVersion}")
    implementation("edu.ucar:grib:${netcdfJavaVersion}")

    runtimeOnly("org.slf4j:slf4j-jdk14:${slf4jVersion}")
    implementation("io.insert-koin:koin-android:3.0.1")
    implementation("io.insert-koin:koin-core:3.0.1")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-xml:$ktorVersion")
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    implementation("androidx.compose.material3:material$material3Version")
    implementation("androidx.compose.material3:material$material3Version")
    implementation("androidx.compose.material3:material3-window-size-class:1.1.2")
    implementation("androidx.compose.material3:material3-adaptive:1.0.0-alpha05")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.0.0-alpha02")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:$navVersion")


    val constants = "11.2.0"

    //MapBox
    implementation("com.mapbox.maps:android:$constants")
    // If you're using compose also add the compose extension
    implementation("com.mapbox.extension:maps-compose:$constants")

    // Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.compose.runtime:runtime-livedata:1.0.5")
    implementation("androidx.compose.runtime:runtime:1.0.0-beta01")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib")


    //const val hilt_compiler = "com.google.dagger:hilt-compiler:${Versions.hilt}"
    ///const val room_compiler = "androidx.room:room-compiler:${Versions.room}"
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    implementation("androidx.core:core-splashscreen:1.0.1")


}