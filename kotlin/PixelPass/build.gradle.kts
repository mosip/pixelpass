import org.jetbrains.kotlin.config.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    `maven-publish`
}
android {
    namespace = "io.mosip.pixelpass"
    compileSdk = 34

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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
        gradleVersion = "8.4"
    }
}

apply {
    from("publish-artifact.gradle")
}
