package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.ProxyBuilder;

class Async {

    public static void main(String[] args) throws InterruptedException {
        HTTPBin httpBin = new ProxyBuilder()
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
