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
    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-opentracing:1.0.1-SNAPSHOT")
    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-json-jackson:1.0.1-SNAPSHOT")
    implementation("org.testcontainers:testcontainers:1.17.3")
    implementation("io.jaegertracing:jaeger-client:1.8.1")
    implementation("ch.qos.logback:logback-classic:1.4.1")
}