plugins {
    java
}

group = "com.github.ljtfreitas.julian-http-client.samples"
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
    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-core:1.0.1-SNAPSHOT")
    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-form-url-encoded-multipart:1.0.1-SNAPSHOT")
//    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-json-jackson:1.0.1-SNAPSHOT")
//    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-xml-jackson:1.0.1-SNAPSHOT")
    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-xml-jaxb:1.0.1-SNAPSHOT")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.0")
}

tasks.compileJava.configure {
    options.compilerArgs.add("-parameters")
}

