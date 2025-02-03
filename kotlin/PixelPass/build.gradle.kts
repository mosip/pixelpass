plugins {
    alias(libs.plugins.androidLibrary)
    kotlin("multiplatform")
    alias(libs.plugins.dokka)
    `maven-publish`
    alias(libs.plugins.sonarqube)
    signing
    jacoco
}
jacoco {
    toolVersion = "0.8.11"
    reportsDirectory = layout.buildDirectory.dir("reports/jacoco")
}

kotlin {
    jvmToolchain(17)

    androidTarget()

    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    applyDefaultHierarchyTemplate()



    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(files("libs/cose-java-1.1.0.jar"))
                implementation(libs.bouncyCastle)
                implementation(libs.upokecenterCbor)
                implementation(libs.eddsa)
                implementation(libs.commonCodec)
                implementation(libs.qrcodegen)
                implementation(libs.base45)
                implementation(libs.cbor)
                implementation(libs.ztzip)
                implementation(libs.google.zxing.javase)
                implementation(libs.org.json)


            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.junit)
                implementation(libs.mockk)
                implementation(libs.json)
            }
        }
        val jvmMain by getting
        val androidMain by getting

    }
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
    implementation(files("libs/cose-java-1.1.0.jar"))
    implementation(libs.bouncyCastle)
    implementation(libs.upokecenterCbor)
    implementation(libs.eddsa)
    implementation(libs.commonCodec)
    implementation(libs.jsonparser)
    implementation(libs.qrcodegen)
    implementation(libs.base45)
    implementation(libs.cbor)
    implementation(libs.ztzip)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.json)
}


tasks.withType<Test> {
    jacoco {
        isEnabled = true
    }
    finalizedBy(tasks.named("jacocoTestReport"))
}

tasks.register("jacocoTestReport", JacocoReport::class) {
    description = "Generates Test coverage report"
    group = "TestReport"
    dependsOn("testDebugUnitTest")

    reports {
        xml.required = true
        html.required = true
    }

    val kotlinTree = fileTree(
        mapOf(
            "dir" to "${layout.buildDirectory.get()}/tmp/kotlin-classes/debug",
            "includes" to listOf("**/*.class")
        )
    )
    val coverageSourceDirs = arrayOf("src/main/java")

    classDirectories.setFrom(files(kotlinTree))
    sourceDirectories.setFrom(coverageSourceDirs)

    executionData.setFrom(files("${layout.buildDirectory.get()}/jacoco/testDebugUnitTest.exec"))
}

tasks {
    register<Wrapper>("wrapper") {
        gradleVersion = "8.5"
    }
}
tasks.register("prepareKotlinBuildScriptModel"){}
tasks.register<Jar>("jarRelease") {
    dependsOn("jvmJar")
}
tasks.named<Jar>("jvmJar") {
    archiveBaseName.set("${project.name}-release")
    archiveVersion.set("0.6.0-SNAPSHOT")
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
}
tasks.register("generatePom") {
    dependsOn("generatePomFileForAarPublication", "generatePomFileForJarReleasePublication")
}

sonarqube {
    properties {
        property( "sonar.java.binaries", "build/intermediates/javac/debug")
        property( "sonar.language", "kotlin")
        property( "sonar.exclusions", "**/build/**, **/*.kt.generated, **/R.java, **/BuildConfig.java")
        property( "sonar.scm.disabled", "true")
        property( "sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
    }
}