package com.github.ljtfreitas.julian.samples;

import com.github.ljtfreitas.julian.Except;
import com.github.ljtfreitas.julian.Promise;
import com.github.ljtfreitas.julian.contract.Body;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.http.HTTPException;
import com.github.ljtfreitas.julian.http.HTTPResponse;
import com.github.ljtfreitas.julian.http.HTTPStatusCode;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.github.ljtfreitas.julian.http.MediaType.APPLICATION_JSON_VALUE;

interface PetsAPI {

    @GET("/pets")
    Collection<Pet> allPets();

    @GET("/pets/{id}")
    Pet onePet(@Path int id);

    @GET("/people/{id}")
    Optional<Pet> onePetAsOptional(@Path int id);

    @GET("/pets/{id}")
    CompletableFuture<Pet> onePetAsFuture(@Path int id);

    @GET("/pets/{id}")
    Promise<Pet, HTTPException> onePetAsPromise(@Path int id);

    @GET("/pets/{id}")
    Except<Pet> onePetAsExcept(@Path int id);

    @GET("/pets/{id}")
    HTTPResponse<Pet> onePetAsHTTPResponse(@Path int id);

    @GET("/pets/{id}")
    CompletableFuture<HTTPResponse<Pet>> onePetAsAsyncHTTPResponse(@Path int id);

    @POST("/pets")
    HTTPStatusCode newPet(@Body(APPLICATION_JSON_VALUE) Pet pet);

    record Pet(String name, String kind, int age) {}
}
