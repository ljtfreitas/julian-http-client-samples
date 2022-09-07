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

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface HTTPBin {

    @GET("/get")
    String get();

    @GET("/get")
    CompletableFuture<String> getAsCompletableFuture();

    @GET("/get")
    Promise<String> getAsPromise();

    @GET("/get")
    HTTPResponse<String> getAsHTTPResponse();

    @GET("/get")
    void get(@Callback Consumer<String> success);

    @POST("/post")
    String post(@Body("text/plain") String bodyAsString);

    @POST("/post")
    void post(@Body("text/plain") String bodyAsString, @Callback Consumer<String> success);

    @PUT("/put")
    String put(@Body("text/plain") String bodyAsString);

    @PUT("/put")
    void put(@Body("text/plain") String bodyAsString, @Callback Consumer<String> success);

    @DELETE("/delete")
    String delete();

    @DELETE("/delete")
    void delete(@Callback Consumer<String> success);

    @GET("/headers")
    void headers(@Header Headers headers, @Callback Consumer<String> success);

    @GET("/headers")
    String headers(@Header Headers headers);
}
