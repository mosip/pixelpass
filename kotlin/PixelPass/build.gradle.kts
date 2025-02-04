plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
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
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn("dokkaJavadoc")
    dependsOn("assembleRelease")
    from("build/intermediates/javac/release/classes") {
        include("**/*.class")
    }
    from("build/tmp/kotlin-classes/release") {
        include("**/*.class")
    }
    manifest {
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] = "0.5.0-SNAPSHOT"
    }
    archiveBaseName.set("${project.name}-release")
    archiveVersion.set("0.5.0-SNAPSHOT")
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
}

tasks.register<Jar>("javadocJar") {
    dependsOn("dokkaJavadoc")
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaHtml").get().outputs.files)
}
tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}
apply(from = "publish-artifact.gradle")
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