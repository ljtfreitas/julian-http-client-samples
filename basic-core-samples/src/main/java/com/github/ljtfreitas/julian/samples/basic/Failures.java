package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.Attempt;
import com.github.ljtfreitas.julian.Kind;
import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.Response;
import com.github.ljtfreitas.julian.http.HTTPClientFailureResponseException;
import com.github.ljtfreitas.julian.http.HTTPResponse;
import com.github.ljtfreitas.julian.http.HTTPResponseException;
import com.github.ljtfreitas.julian.http.HTTPServerFailureResponseException;
import com.github.ljtfreitas.julian.http.HTTPServerFailureResponseException.GatewayTimeout;
import com.github.ljtfreitas.julian.http.HTTPServerFailureResponseException.InternalServerError;
import com.github.ljtfreitas.julian.http.HTTPStatusCode;
import com.github.ljtfreitas.julian.http.HTTPStatusGroup;
import com.github.ljtfreitas.julian.http.RecoverableHTTPResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

class Failures {

    public static void main(String[] args) {
        HTTPBin httpBin = new ProxyBuilder()
                .build(HTTPBin.class, "https://httpbin.org");

        // all possible HTTP client errors (4xx)
        String clientErrors = range(400, 500).mapToObj(Integer::toString).collect(joining(","));

        // all possible HTTP server errors (5xx)
        String serverErrors = range(500, 600).mapToObj(Integer::toString).collect(joining(","));

        // httpbin is going to response one of these HTTP statuses, randomly chosen

        System.out.println("4xx status code: " + httpBin.status(clientErrors)
                .onFailure(Throwable::printStackTrace));
        System.out.println("#############");

        System.out.println("5xx status code: " + httpBin.status(serverErrors)
                .onFailure(Throwable::printStackTrace));
        System.out.println("#############");

        // http response failures can be manually handled...
        httpBin.status("400")
                .recover(HTTPStatusCode.BAD_REQUEST, ((status, headers, body) -> {
                    System.out.println("BadRequest: " + status + ", " + headers + "\n" + new String(body));
                    return "a fallback value for BadRequest cases";
                }))
                .onSuccess(value -> System.out.println("Recovered from BadRequest: " + value));
        System.out.println("#############");

        httpBin.status("500")
                .recover(InternalServerError.class, e -> {
                    System.out.println("InternalServerError: " + e.status() + ", " + e.headers() + "\n" + e.bodyAsString());
                    return "a fallback value for InternalServerError cases";
                })
                .onSuccess(value -> System.out.println("Recovered from InternalServerError: " + value));
        System.out.println("#############");

        HTTPResponse<String> gatewayTimeoutResponse = httpBin.status("504");

        // and we can deserialize the failure response body for another type
        // RecoverableHTTPResponse is the default response type for failures
        gatewayTimeoutResponse
                .cast(new Kind<RecoverableHTTPResponse<String>>() {
                })
                .map(r -> r.recover(String.class))
                .ifPresentOrElse(r -> r.onSuccess(body -> System.out.println("HTTP response body: " + body)),
                        () -> System.out.println("The response isn't recoverable :("));
        System.out.println("#############");

        // the response body is an Attempt<T> value, because a body of type T will be available for success cases,
        // and will not be available for failure ones (T is supposed to be a target type for success).
        Attempt<String> attempt = gatewayTimeoutResponse.body();

        // if we try to get the body from a failure response, an exception will be throw;
        // so we can handle in an usual try/catch block
        try {
            String body = attempt.unsafe();
            System.out.println("The response body: " + body);

        } catch (GatewayTimeout e) { // an exception for this specific response
            e.printStackTrace();

        } catch (HTTPClientFailureResponseException e) { // any 4xx responses
            e.printStackTrace();

        } catch (HTTPServerFailureResponseException e) { // any 5xx responses
            e.printStackTrace();
        } catch (HTTPResponseException e) { // any http failed response (4xx or 5xx)
            e.printStackTrace();
        }

        System.out.println("#############");
    }
}
