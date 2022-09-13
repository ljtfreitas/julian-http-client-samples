plugins {
	id("org.springframework.boot") version "2.7.4"
	id("io.spring.dependency-management") version "1.0.14.RELEASE"
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
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("com.github.ljtfreitas.julian-http-client:julian-http-client-spring-starter:1.0.1-SNAPSHOT")
}

