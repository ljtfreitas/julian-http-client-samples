package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.Headers;
import com.github.ljtfreitas.julian.Promise;
import com.github.ljtfreitas.julian.contract.Callback;
import com.github.ljtfreitas.julian.contract.DELETE;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.Header;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.PUT;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.contract.TextPlainContent;
import com.github.ljtfreitas.julian.http.HTTPHeaders;
import com.github.ljtfreitas.julian.http.HTTPResponse;
import com.github.ljtfreitas.julian.http.HTTPStatus;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

interface HTTPBin {

    @GET("/get")
    String get();

    @GET("/get")
    CompletableFuture<String> getAsCompletableFuture();

    @GET("/get")
    Promise<String> getAsPromise();

    @GET("/get")
    HTTPResponse<String> getAsHTTPResponse();

    @GET("/get")
    void getAsCallback(@Callback Consumer<String> success);

    @POST("/post")
    HTTPStatus post(@TextPlainContent String bodyAsString);

    @POST("/post")
    void postAsCallback(@TextPlainContent String bodyAsString, @Callback Consumer<HTTPStatus> success);

    @PUT("/put")
    HTTPStatus put(@TextPlainContent String bodyAsString);

    @PUT("/put")
    void putAsCallback(@TextPlainContent String bodyAsString, @Callback Consumer<HTTPStatus> success);

    @DELETE("/delete")
    HTTPStatus delete();

    @DELETE("/delete")
    void deleteAsCallback(@Callback Consumer<HTTPStatus> success);

    @GET("/headers")
    void headersAsCallback(@Header Headers headers, @Callback Consumer<HTTPHeaders> success);

    @GET("/headers")
    HTTPHeaders headers(@Header Headers headers);

    @GET("/status/{codes}")
    HTTPResponse<String> status(@Path String codes);

    @GET("/delay/{delay}")
    Promise<HTTPStatus> delay(@Path int delay);
}
