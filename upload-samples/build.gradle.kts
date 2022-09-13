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
    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-form-url-encoded-multipart:1.0.1-SNAPSHOT")
    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-json-jackson:1.0.1-SNAPSHOT")
}
