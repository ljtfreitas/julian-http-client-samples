package com.github.ljtfreitas.julian.samples.resilience4j;

import com.github.ljtfreitas.julian.Attempt;
import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.JsonContent;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.http.client.HTTPClientException;
import com.github.ljtfreitas.julian.http.resilience4j.RateLimiterHTTPRequestInterceptor;
import com.github.ljtfreitas.julian.http.resilience4j.RetryHTTPRequestInterceptor;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class RetrySamples {

    public static void main(String[] args) throws InterruptedException {
        Retry retry = Retry.of("my-retry", RetryConfig.custom()
                .maxAttempts(3)
                .retryOnException(e -> e instanceof HTTPClientException)
                .waitDuration(Duration.ofMillis(5000))
                .build());

        // we need a ScheduledExecutorService because the retry doesn't block the thread
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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
                        .add(new RetryHTTPRequestInterceptor(retry, scheduler))
                    .and()
                .and()
                .build(PetsApi.class, "http://localhost:7070");

        petsApi.create(new NewPet("Puka", 2))
                .onSuccess(pet -> System.out.println("a new pet was created: " + pet + "...")) // this call will work...
                .onFailure(Throwable::printStackTrace);

        System.out.println("#########");

        // now, let's stop the server...

        Thread.sleep(5000);

        // the next request will fail; then we will try 3 times

        petsApi.create(new NewPet("Puka", 2))
                .onSuccess(pet -> System.out.println("a new pet was created: " + pet + "...")) // no, it's not
                .onFailure(Throwable::printStackTrace); // it will fail (3 times)

        System.out.println("#########");

        scheduler.shutdown();
    }

    @Path("/pets")
    interface PetsApi {

        @POST
        Attempt<Pet> create(@JsonContent NewPet pet);

    }

    record NewPet(String name, int age) {}

    record Pet(int id, String name, int age) {}

}


