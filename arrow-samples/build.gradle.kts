plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
}

tasks.compileKotlin.configure {

    kotlinOptions {
        jvmTarget = "11"
        javaParameters = true
    }
}

dependencies {
    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-core:1.0.1-SNAPSHOT")
    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-arrow:1.0.1-SNAPSHOT")
    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-json-kotlin:1.0.1-SNAPSHOT")
    implementation(kotlin("stdlib"))
}