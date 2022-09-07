package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.Header;
import com.github.ljtfreitas.julian.Headers;
import com.github.ljtfreitas.julian.ProxyBuilder;

public class Callbacks {

    public static void main(String[] args) throws InterruptedException {
        HTTPBin httpBin = new ProxyBuilder().build(HTTPBin.class, "https://httpbin.org");

        httpBin.get(result -> {
            System.out.println("GET: " + result);
            System.out.println("#############");
        });

        httpBin.post("hello", result -> {
            System.out.println("POST: " + result);
            System.out.println("#############");
        });

        httpBin.put("hello", result -> {
            System.out.println("PUT:" + result);
            System.out.println("#############");
        });

        httpBin.delete(result -> {
            System.out.println("DELETE: " + result);
            System.out.println("#############");
        });

        httpBin.headers(Headers.create(
                new Header("x-my-header", "whatever"),
                new Header("x-my-other-header", "whatever")
        ), result -> {
            System.out.println("Headers: " + result);
            System.out.println("#############");
        });

        Thread.sleep(2000);
    }
}
