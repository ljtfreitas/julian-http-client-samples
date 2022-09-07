package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.http.HTTPResponse;

import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

public class Failures {

    public static void main(String[] args) {
        FailuresHTTPBin httpBin = new ProxyBuilder().build(FailuresHTTPBin.class, "https://httpbin.org");

        String clientErrors = range(400, 500).mapToObj(Integer::toString).collect(joining(","));
        String serverErrors = range(500, 600).mapToObj(Integer::toString).collect(joining(","));

        System.out.println("4xx status code: " + httpBin.errors(clientErrors));
        System.out.println("#############");

        System.out.println("5xx status code: " + httpBin.errors(serverErrors));
        System.out.println("#############");
    }

    public interface FailuresHTTPBin {

        @GET("/status/{codes}")
        HTTPResponse<String> errors(@Path String codes);

    }
}
