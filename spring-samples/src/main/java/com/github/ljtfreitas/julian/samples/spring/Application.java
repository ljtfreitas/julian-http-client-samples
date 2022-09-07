package com.github.ljtfreitas.julian.samples.spring;

import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.http.spring.webflux.WebClientHTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Bean
	WebClient webClient(WebClient.Builder builder) {
		return builder.build();
	}

	@Bean
	HTTPBin httpBin(WebClient webClient) {
		return new ProxyBuilder()
				.http()
					.with(new WebClientHTTP(webClient))
				.build(HTTPBin.class, "https://httpbin.org");
	}

	@Bean
	RouterFunction<ServerResponse> routes(HTTPBin httpBin) {
		return route(GET("/"), r -> {
			log.info("hello");

			return httpBin.get().flatMap(response -> ok().bodyValue(response));
		});
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	public interface HTTPBin {

		@GET("/get")
		Mono<String> get();

	}
}
