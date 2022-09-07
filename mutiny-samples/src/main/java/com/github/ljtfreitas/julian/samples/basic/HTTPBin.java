package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.contract.Body;
import com.github.ljtfreitas.julian.contract.DELETE;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.PUT;
import io.smallrye.mutiny.Uni;

public interface HTTPBin {

    @GET("/get")
    Uni<String> get();

    @POST("/post")
    Uni<String> post(@Body("text/plain") String bodyAsString);

    @PUT("/put")
    Uni<String> put(@Body("text/plain") String bodyAsString);

    @DELETE("/delete")
    Uni<String> delete();
}
