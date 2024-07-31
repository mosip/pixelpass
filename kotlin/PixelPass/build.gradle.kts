import org.jetbrains.kotlin.config.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    `maven-publish`
    `signing`
}
android {
    namespace = "io.mosip.pixelpass"
    compileSdk = 33

    defaultConfig {
        minSdk = 23
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.qrcodegen)
    implementation(libs.base45)
    implementation(libs.cbor)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation("org.json:json:20140107")
}

tasks {
    register<Wrapper>("wrapper") {
        gradleVersion = "8.5"
    }
}

apply {
    from("publish-artifact.gradle")
}
