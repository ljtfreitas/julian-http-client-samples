package com.github.ljtfreitas.julian.samples.vavr;

import com.github.ljtfreitas.julian.ProxyBuilder;

public class Basic {

    public static void main(String[] args) {
        HTTPBin httpBin = new ProxyBuilder().build(HTTPBin.class, "https://httpbin.org");

        httpBin.statusAsTry("200,201,202")
                .onSuccess(response -> {
                    System.out.println("Success: " + response);
                    System.out.println("#############");
                });

        httpBin.statusAsTry("400,401,404")
                .onFailure(e -> {
                    System.out.println("Failure: " + e.getMessage());
                    System.out.println("#############");
                });

        httpBin.statusAsEither("200,201,202")
                .peek(status -> {
                    System.out.println("Success: " + status);
                    System.out.println("#############");
                });

        httpBin.statusAsEither("400,401,404")
                .peekLeft(e -> {
                    System.out.println("Failure: " + e.getMessage());
                    System.out.println("#############");
                });

    }
}
