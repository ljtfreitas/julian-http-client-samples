package com.github.ljtfreitas.julian.samples.resilience4j;

import com.github.ljtfreitas.julian.Attempt;
import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.JsonContent;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.http.resilience4j.CircuitBreakerHTTPRequestInterceptor;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

import java.time.Duration;

public class CircuitBreakerSamples {

    public static void main(String[] args) throws InterruptedException {
        // stop the pets-api server before running this sample

        CircuitBreaker circuitBreaker = CircuitBreaker.of("my-circuit-breaker", CircuitBreakerConfig.custom()
                .minimumNumberOfCalls(1) // with only 1 call...
                .failureRateThreshold(100) // that will fail,
                .waitDurationInOpenState(Duration.ofMillis(5000)) // the circuit breaker will remain open by 5 seconds
                .build());

        PetsApi petsApi = new ProxyBuilder()
                .http()
                    .client()
                        .extensions()
                            .debug()
                                .enabled() // enable HTTP request/response logging
                            .and()
                        .and()
                    .and()
                    .interceptors()
                        .add(new CircuitBreakerHTTPRequestInterceptor(circuitBreaker))
                    .and()
                .and()
                .build(PetsApi.class, "http://localhost:7070");

        petsApi.create(new NewPet("Puka", 2))
                .onSuccess(pet -> System.out.println("a new pet was created: " + pet + "...")) // no, it's not
                .onFailure(Throwable::printStackTrace); // it will fail

        System.out.println("#########");

        // now the circuit breaker is open; the next request will be rejected

        petsApi.create(new NewPet("Puka", 2))
                .onSuccess(pet -> System.out.println("a new pet was created: " + pet + "...")) // no, it's not again
                .onFailure(Throwable::printStackTrace);

        System.out.println("#########");

        // now, let's start the server...we wait

        Thread.sleep(10000);

        petsApi.create(new NewPet("Puka", 2))
                .onSuccess(pet -> System.out.println("a new pet was created: " + pet + "...")) // yes, it will work
                .onFailure(Throwable::printStackTrace);
    }

    @Path("/pets")
    interface PetsApi {

        @POST
        Attempt<Pet> create(@JsonContent NewPet pet);

    }

    record NewPet(String name, int age) {}

    record Pet(int id, String name, int age) {}

}


