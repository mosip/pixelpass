import org.gradle.api.tasks.bundling.Jar
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


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.bouncyCastle)
                implementation(libs.upokecenterCbor)
                implementation(libs.eddsa)
                implementation(libs.commonCodec)
                implementation(libs.qrcodegen)
                implementation(libs.base45)
                implementation(libs.cbor)
                implementation(libs.ztzip)
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
        val jvmTest by getting
        val androidUnitTest by getting

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
}

tasks.withType<AbstractPublishToMaven>().configureEach {
    onlyIf {
        publication.name in listOf("aar", "jarRelease")
    }
}

tasks.register("jacocoMergedReport", JacocoReport::class) {
    dependsOn("jvmTest", "testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        xml.outputLocation.set(file("${layout.buildDirectory.get()}/reports/jacoco/jacocoMergedReport/jacocoMergedReport.xml"))
        html.outputLocation.set(file("${layout.buildDirectory.get()}/reports/jacoco/jacocoMergedReport/html"))
    }

    doFirst {
        val tempDir = file("${layout.buildDirectory.get()}/tmp/jacoco-merged")
        tempDir.deleteRecursively()
        tempDir.mkdirs()

        val uniqueClasses = mutableSetOf<String>()
        val classFiles = mutableListOf<File>()

        val jvmDir = file("${layout.buildDirectory.get()}/classes/kotlin/jvm/main")
        if (jvmDir.exists()) {
            jvmDir.walkTopDown()
                .filter { it.isFile && it.name.endsWith(".class") }
                .forEach { classFile ->
                    val relativePath = classFile.relativeTo(jvmDir).path
                    if (uniqueClasses.add(relativePath)) {
                        val targetFile = File(tempDir, relativePath)
                        targetFile.parentFile.mkdirs()
                        classFile.copyTo(targetFile)
                        classFiles.add(targetFile)
                    }
                }
        }


        val androidDir = file("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug")
        if (androidDir.exists()) {
            androidDir.walkTopDown()
                .filter { it.isFile && it.name.endsWith(".class") }
                .forEach { classFile ->
                    val relativePath = classFile.relativeTo(androidDir).path
                    if (uniqueClasses.add(relativePath)) {
                        val targetFile = File(tempDir, relativePath)
                        targetFile.parentFile.mkdirs()
                        classFile.copyTo(targetFile)
                        classFiles.add(targetFile)
                    }
                }
        }
    }

    classDirectories.setFrom(
        fileTree("${layout.buildDirectory.get()}/tmp/jacoco-merged") {
            exclude(
                "**/*Test*.*",
                "**/*\$WhenMappings.*",
                "**/*\$Companion.*",
                "**/R.class",
                "**/R$*.class",
                "**/BuildConfig.*",
                "**/Manifest*.*",
                "android/**/*.*"
            )
        }
    )

    sourceDirectories.setFrom(files(
        "src/commonMain/kotlin",
        "src/jvmMain/kotlin",
        "src/androidMain/kotlin"
    ))

    executionData.setFrom(files(
        "${layout.buildDirectory.get()}/jacoco/jvmTest.exec",
        "${layout.buildDirectory.get()}/jacoco/testDebugUnitTest.exec"
    ))
}

tasks.withType<Test> {
    jacoco {
        isEnabled = true
    }
    finalizedBy(tasks.named("jacocoMergedReport"))

}



tasks {
    register<Wrapper>("wrapper") {
        gradleVersion = "8.5"
    }
}
tasks.register("prepareKotlinBuildScriptModel"){}
tasks.register<Jar>("jarRelease") {
    dependsOn("dokkaJavadoc")
    dependsOn("assembleRelease")
    dependsOn("jvmJar")
}

tasks.register<Jar>("javadocJar") {
    dependsOn("dokkaJavadoc")
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaHtml").get().outputs.files)
}
tasks.register("generatePom") {
    dependsOn("generatePomFileForAarPublication", "generatePomFileForJarReleasePublication")
}

apply(from = "publish-artifact.gradle")

var buildDir = project.layout.buildDirectory.get()
sonarqube {
    properties {
        property("sonar.java.binaries", "$buildDir/classes/kotlin/jvm/main, $buildDir/tmp/kotlin-classes/debug")
        property("sonar.language", "kotlin")
        property("sonar.exclusions", "**/build/**, **/*.kt.generated, **/R.java, **/BuildConfig.java")
        property("sonar.scm.disabled", "true")
        property("sonar.coverage.jacoco.xmlReportPaths",
            "$buildDir/reports/jacoco/jacocoMergedReport/jacocoMergedReport.xml")
        property("sonar.sources", "src/commonMain/kotlin,src/jvmMain/kotlin,src/androidMain/kotlin")
        property("sonar.tests", "src/commonTest/kotlin,src/jvmTest/kotlin,src/androidUnitTest/kotlin")
    }
}
tasks.withType<Jar>().configureEach {
    doLast {
        ant.withGroovyBuilder {
            "checksum"(
                "algorithm" to "md5",
                "file" to archiveFile.get().asFile
            )
            "checksum"(
                "algorithm" to "sha1",
                "file" to archiveFile.get().asFile
            )
        }
    }
}

