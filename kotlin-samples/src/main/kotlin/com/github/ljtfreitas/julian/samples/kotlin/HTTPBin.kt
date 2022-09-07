package com.github.ljtfreitas.julian.samples.kotlin

import com.github.ljtfreitas.julian.Promise
import com.github.ljtfreitas.julian.contract.Body
import com.github.ljtfreitas.julian.contract.Callback
import com.github.ljtfreitas.julian.contract.DELETE
import com.github.ljtfreitas.julian.contract.GET
import com.github.ljtfreitas.julian.contract.POST
import com.github.ljtfreitas.julian.contract.PUT
import com.github.ljtfreitas.julian.http.HTTPResponse
import java.util.concurrent.CompletableFuture

interface HTTPBin {

    @GET("/get")
    fun get(): String

    @GET("/get")
    fun get(@Callback success: (String) -> Unit)

    @POST("/post")
    fun post(@Body("text/plain") bodyAsString: String): String

    @POST("/post")
    fun post(@Body("text/plain") bodyAsString: String, @Callback success: (String) -> Unit)

    @PUT("/put")
    fun put(@Body("text/plain") bodyAsString: String): String

    @PUT("/put")
    fun put(@Body("text/plain") bodyAsString: String, @Callback success: (String) -> Unit)

    @DELETE("/delete")
    fun delete(): String

    @DELETE("/delete")
    fun delete(@Callback success: (String) -> Unit)
}