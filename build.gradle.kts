plugins {
    kotlin("jvm") version "1.5.10"
    java
}

group = "com.github.ljtfreitas.julian-http-client"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()

    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        mavenContent {
            snapshotsOnly()
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-core:0.0.7-SNAPSHOT")
    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-json-jackson:0.0.7-SNAPSHOT")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.0")
    implementation("org.mock-server:mockserver-netty:5.11.1")
    implementation("ch.qos.logback:logback-classic:1.2.3")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

tasks.compileJava.configure {
    options.compilerArgs.add("-parameters")
}

