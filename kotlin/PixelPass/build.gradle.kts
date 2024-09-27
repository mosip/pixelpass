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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.qrcodegen)
    implementation(libs.base45)
    implementation(libs.cbor)
    implementation(libs.ztzip)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.json)
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
        attributes["Implementation-Version"] = "1.5-SNAPSHOT"
    }
    archiveBaseName.set("${project.name}-release")
    archiveVersion.set("1.5-SNAPSHOT")
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
}
apply(from = "publish-artifact.gradle")
tasks.register("generatePom") {
    dependsOn("generatePomFileForAarPublication", "generatePomFileForJarReleasePublication")
}