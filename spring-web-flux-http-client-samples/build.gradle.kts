plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.compileJava.configure {
    val arguments = options.compilerArgs
    arguments += "-parameters"
}

dependencies {
    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-core:1.0.1-SNAPSHOT")
    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-http-spring-web-flux:1.0.1-SNAPSHOT")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4")
}