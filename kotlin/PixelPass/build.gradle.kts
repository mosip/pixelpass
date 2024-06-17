import org.jetbrains.kotlin.config.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    `maven-publish`
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
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.material)
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

tasks.register<Jar>("jarRelease") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn("assembleRelease")
    from("build/intermediates/javac/release/classes") {
        include("**/*.class")
    }
    from("build/tmp/kotlin-classes/release") {
        include("**/*.class")
    }
    manifest {
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] = "1.3-SNAPSHOT-JAR"
    }
    archiveBaseName.set("${project.name}-release")
    archiveVersion.set("1.3-SNAPSHOT-JAR")
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
}
apply(from = "publish-artifact.gradle")
tasks.register("generatePom") {
    dependsOn("generatePomFileForAarPublication", "generatePomFileForJarReleasePublication")
}