package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.Authorization;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.http.HTTPResponse;
import com.github.ljtfreitas.julian.http.auth.Authentication;
import com.github.ljtfreitas.julian.http.auth.BasicAuthentication;
import com.github.ljtfreitas.julian.http.auth.BearerAuthentication;

class Authenticated {

    public static void main(String[] args) throws InterruptedException {
        AuthenticatedHTTPBin httpBin = new ProxyBuilder()
                .build(AuthenticatedHTTPBin.class, "https://httpbin.org");

        httpBin.basic("user", "password", new BasicAuthentication("user", "password"))
                .onSuccess(((status, headers, body) -> System.out.println("Basic: " + status + ", " + headers + "\n" + body)))
                .onFailure(Throwable::printStackTrace);
        System.out.println("#############");

        httpBin.bearer(new BearerAuthentication("user"))
                .onSuccess((status, headers, body) -> System.out.println("Bearer: " + status + ", " + headers + "\n" + body))
                .onFailure(Throwable::printStackTrace);

        // authentication failed - wrong credentials
        httpBin.basic("user", "password", new BasicAuthentication("other-user", "other-password"))
                .onFailure(Throwable::printStackTrace);

        System.out.println("#############");

        Thread.sleep(1000);
    }

    interface AuthenticatedHTTPBin {

        @GET("/basic-auth/{user}/{password}")
        HTTPResponse<String> basic(@Path String user, @Path String password, @Authorization Authentication authentication);

        @GET("/bearer")
        HTTPResponse<String> bearer(@Authorization Authentication authentication);
    }

}
