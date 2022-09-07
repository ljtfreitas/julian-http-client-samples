package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.http.HTTPResponse;

public class HTTPResponseMetadata {

    public static void main(String[] args) {
        HTTPBin httpBin = new ProxyBuilder().build(HTTPBin.class, "https://httpbin.org");

        HTTPResponse<String> response = httpBin.getAsHTTPResponse();

        System.out.println("Status: " + response.status()
                + "\n" + "Headers: " + response.headers()
                + "\n" + "Body: " + response.body().unsafe());

        System.out.println("#############");

    }
}
