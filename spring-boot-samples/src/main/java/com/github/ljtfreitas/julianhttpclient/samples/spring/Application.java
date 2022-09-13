package com.github.ljtfreitas.julianhttpclient.samples.spring;

import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.spring.autoconfigure.JulianHTTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	HTTPBin httpBin;

	@Override
	public void run(String... args) {
		System.out.println(httpBin.get());
	}

	@JulianHTTPClient(name = "httpBin")
	interface HTTPBin {

		@GET("/get")
		String get();
	}
}
