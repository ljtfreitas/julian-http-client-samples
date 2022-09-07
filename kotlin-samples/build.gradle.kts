import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "com.github.ljtfreitas.julian-http-client.samples"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()

    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        mavenContent {
            snapshotsOnly()
        }
    }
}

dependencies {
    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-core:0.0.6-SNAPSHOT")
    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-kotlin:0.0.6-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}