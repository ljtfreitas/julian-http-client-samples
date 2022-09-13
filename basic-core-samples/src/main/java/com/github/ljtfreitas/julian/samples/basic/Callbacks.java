package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.Header;
import com.github.ljtfreitas.julian.Headers;
import com.github.ljtfreitas.julian.ProxyBuilder;

class Callbacks {

    public static void main(String[] args) throws InterruptedException {
        HTTPBin httpBin = new ProxyBuilder()
                .build(HTTPBin.class, "https://httpbin.org");

        httpBin.getAsCallback(result -> {
            System.out.println("GET: " + result);
            System.out.println("#############");
        });

        httpBin.postAsCallback("hello", result -> {
            System.out.println("POST: " + result);
            System.out.println("#############");
        });

        httpBin.putAsCallback("hello", result -> {
            System.out.println("PUT: " + result);
            System.out.println("#############");
        });

        httpBin.deleteAsCallback(result -> {
            System.out.println("DELETE: " + result);
            System.out.println("#############");
        });

        Headers headers = Headers.create(new Header("x-my-header", "whatever"), new Header("x-my-other-header", "whatever"));

        httpBin.headersAsCallback(headers, result -> {
            System.out.println("Headers: " + result);
            System.out.println("#############");
        });

        Thread.sleep(3000);

        System.out.println("#############");
    }

}
