package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.Authorization;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.http.auth.Authentication;
import com.github.ljtfreitas.julian.http.auth.BasicAuthentication;
import com.github.ljtfreitas.julian.http.auth.BearerAuthentication;

public class Authenticated {

    public static void main(String[] args) {
        AuthenticatedHTTPBin httpBin = new ProxyBuilder()
                .build(AuthenticatedHTTPBin.class, "https://httpbin.org");

        System.out.println("Basic: " + httpBin.basic("user", "password", new BasicAuthentication("user", "password")));
        System.out.println("#############");

        System.out.println("Bearer: " + httpBin.bearer(new BearerAuthentication("user")));
        System.out.println("#############");
    }

    public interface AuthenticatedHTTPBin {

        @GET("/basic-auth/{user}/{password}")
        String basic(@Path("user") String user, @Path("password") String password, @Authorization Authentication authentication);

        @GET("/bearer")
        String bearer(@Authorization Authentication authentication);
    }
}
