package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.ProxyBuilder;

import java.time.Duration;

class Timeout {

    public static void main(String[] args) {
        HTTPBin httpBin = new ProxyBuilder()
                .http()
                    .client()
                        .configure()
                            .requestTimeout(Duration.ofSeconds(3))
                        .and()
                    .and()
                .and()
                .build(HTTPBin.class, "https://httpbin.org");

        httpBin.delay(5) // 5 seconds delay
                .onFailure(Throwable::printStackTrace) // timeout exception
                .join();

        System.out.println("#############");

        httpBin.delay(1) // 1 second delay; ok, because our timeout is 3 seconds
                .onSuccess(status -> System.out.println("HTTP status: " + status))
                .onFailure(Throwable::printStackTrace)
                .join();

    }

}
