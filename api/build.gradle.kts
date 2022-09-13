plugins {
    kotlin("jvm") version "1.7.10"
}

group = "com.github.ljtfreitas.julian-http-client.samples"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javalin:javalin:4.6.4")
    implementation("org.slf4j:slf4j-simple:2.0.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.4")
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))
}