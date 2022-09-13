package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.Header;
import com.github.ljtfreitas.julian.Headers;
import com.github.ljtfreitas.julian.ProxyBuilder;

class Basic {

    public static void main(String[] args) {
        HTTPBin httpBin = new ProxyBuilder()
                .build(HTTPBin.class, "https://httpbin.org");

        System.out.println("GET: " + httpBin.get());
        System.out.println("#############");

        System.out.println("POST: " + httpBin.post("hello"));
        System.out.println("#############");

        System.out.println("PUT: " + httpBin.put("hello"));
        System.out.println("#############");

        System.out.println("DELETE: " + httpBin.delete());
        System.out.println("#############");

        Headers headers = Headers.create(new Header("x-my-header", "whatever"), new Header("x-my-other-header", "whatever"));
        System.out.println("Headers: " + httpBin.headers(headers));
        System.out.println("#############");

    }

}
