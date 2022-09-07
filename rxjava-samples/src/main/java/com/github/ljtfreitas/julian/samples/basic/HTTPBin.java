package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.Headers;
import com.github.ljtfreitas.julian.Promise;
import com.github.ljtfreitas.julian.contract.Body;
import com.github.ljtfreitas.julian.contract.Callback;
import com.github.ljtfreitas.julian.contract.DELETE;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.Header;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.PUT;
import com.github.ljtfreitas.julian.http.HTTPResponse;
import io.reactivex.rxjava3.core.Single;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface HTTPBin {

    @GET("/get")
    Single<String> get();

    @POST("/post")
    Single<String> post(@Body("text/plain") String bodyAsString);

    @PUT("/put")
    Single<String> put(@Body("text/plain") String bodyAsString);

    @DELETE("/delete")
    Single<String> delete();
}
