package com.github.ljtfreitas.julian.samples;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ljtfreitas.julian.Except;
import com.github.ljtfreitas.julian.Promise;
import com.github.ljtfreitas.julian.contract.Callback;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.http.HTTPException;
import com.github.ljtfreitas.julian.http.HTTPResponse;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Path("/api")
interface StarWarsAPI {

    @GET("/people")
    AllPeople allPeople();

    @GET("/people")
    void allPeopleUsingCallback(@Callback Consumer<AllPeople> success);

    @GET("/people/{id}")
    Person onePeople(@Path int id);

    @GET("/people/{id}")
    void onePeopleUsingCallback(@Path int id, @Callback Consumer<Person> success);

    @GET("/people/{id}")
    Optional<Person> onePeopleAsOptional(@Path int id);

    @GET("/people/{id}")
    CompletableFuture<Person> onePeopleAsFuture(@Path int id);

    @GET("/people/{id}")
    Promise<Person, HTTPException> onePeopleAsPromise(@Path int id);

    @GET("/people/{id}")
    Except<Person> onePeopleAsExcept(@Path int id);

    @GET("/people/{id}")
    HTTPResponse<Person> onePeopleAsHTTPResponse(@Path int id);

    @GET("/people/{id}")
    CompletableFuture<HTTPResponse<Person>> onePeopleAsAsyncHTTPResponse(@Path int id);

    record AllPeople(int count, Collection<Person> results) {}

    record Person(String name, @JsonProperty("birth_year") String birthYear, ZonedDateTime created, URL url) {}
}
