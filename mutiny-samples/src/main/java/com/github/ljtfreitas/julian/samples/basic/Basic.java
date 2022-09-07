package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.ProxyBuilder;

public class Basic {

    public static void main(String[] args) throws InterruptedException {
        HTTPBin httpBin = new ProxyBuilder().build(HTTPBin.class, "https://httpbin.org");

        httpBin.get().subscribe().with(result -> {
            System.out.println("GET: " + result);
            System.out.println("#############");
        });

        httpBin.post("hello").subscribe().with(result -> {
            System.out.println("POST: " + result);
            System.out.println("#############");
        });

        httpBin.put("hello").subscribe().with(result -> {
            System.out.println("PUT:" + result);
            System.out.println("#############");
        });

        httpBin.delete().subscribe().with(result -> {
            System.out.println("DELETE: " + result);
            System.out.println("#############");
        });

        Thread.sleep(2000);
    }
}
