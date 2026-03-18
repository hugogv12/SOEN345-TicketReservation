import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.reader()?.use { load(it) }
}

fun String.escapeForBuildConfig(): String =
    "\"" + replace("\\", "\\\\").replace("\"", "\\\"") + "\""

android {
    namespace = "com.example.ticket_reservation"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.ticket_reservation"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        val supabaseUrl = localProperties.getProperty("supabase.url", "").trim()
        val supabaseAnon = localProperties.getProperty("supabase.anon.key", "").trim()
        buildConfigField("String", "SUPABASE_URL", supabaseUrl.escapeForBuildConfig())
        buildConfigField("String", "SUPABASE_ANON_KEY", supabaseAnon.escapeForBuildConfig())

        testInstrumentationRunner = "com.example.ticket_reservation.TicketTestRunner"
        testInstrumentationRunnerArguments["disableAnimations"] = "true"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.okhttp)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.json)
    testImplementation(libs.mockwebserver)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.contrib)
    androidTestImplementation(libs.espresso.intents)
}