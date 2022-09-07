plugins {
    java
}

group = "com.github.ljtfreitas.julian-http-client.samples"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

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
    implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-mutiny:0.0.6-SNAPSHOT")
}
