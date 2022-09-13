package com.github.ljtfreitas.julian.samples.resilience4j;

import com.github.ljtfreitas.julian.Attempt;
import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.JsonContent;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.http.resilience4j.RateLimiterHTTPRequestInterceptor;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;

import java.time.Duration;

public class RateLimiterSamples {

    public static void main(String[] args) throws InterruptedException {
        RateLimiter rateLimiter = RateLimiter.of("my-rate-limiter", RateLimiterConfig.custom()
                .limitForPeriod(1) // only 1 call in the period
                .timeoutDuration(Duration.ofMillis(50))
                .limitRefreshPeriod(Duration.ofMillis(1000)) // a 1 second window
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
                        .add(new RateLimiterHTTPRequestInterceptor(rateLimiter))
                    .and()
                .and()
                .build(PetsApi.class, "http://localhost:7070");

        petsApi.create(new NewPet("Puka", 2))
                .onSuccess(pet -> System.out.println("a new pet was created: " + pet + "...")) // this call will work...
                .onFailure(Throwable::printStackTrace);

        System.out.println("#########");

        // now the rate limiter permissions reach the limit; the next request will be rejected

        petsApi.create(new NewPet("Puka", 2))
                .onSuccess(pet -> System.out.println("a new pet was created: " + pet + "...")) // no, it's not
                .onFailure(Throwable::printStackTrace); // it will fail (too many requests)

        System.out.println("#########");

        // now, let's start the server...we wait

        Thread.sleep(5000);

        // after the refresh period we can run requests again

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


