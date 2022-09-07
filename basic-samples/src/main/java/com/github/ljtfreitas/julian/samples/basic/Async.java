package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.ProxyBuilder;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Async {

    public static void main(String[] args) throws InterruptedException {
        Executor myThreadPool = Executors.newFixedThreadPool(100);

        HTTPBin httpBin = new ProxyBuilder()
                .http()
                    .client()
                        .configure()
                            .executor(myThreadPool)
                        .and()
                    .and()
                .and()
                .build(HTTPBin.class, "https://httpbin.org");

        httpBin.getAsCompletableFuture().thenAccept(result -> {
            System.out.println("GET: " + result);
            System.out.println("#############");
        });

        httpBin.getAsPromise().onSuccess(result -> {
            System.out.println("GET: " + result);
            System.out.println("#############");
        });

        Thread.sleep(2000);
    }
}
