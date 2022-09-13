package com.github.ljtfreitas.julian.samples.vavr;

import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.DELETE;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.JsonContent;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.PUT;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.http.HTTPResponse;
import com.github.ljtfreitas.julian.http.HTTPResponseException;
import com.github.ljtfreitas.julian.http.HTTPResponseFailure;
import com.github.ljtfreitas.julian.http.HTTPStatus;
import com.github.ljtfreitas.julian.http.HTTPStatusCode;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.util.Collection;
import java.util.stream.Collectors;

public class VavrSamples {

    public static void main(String[] args) {
        PetsApi petsApi = new ProxyBuilder()
                .http()
                    .failure()
                        .when(HTTPStatusCode.NOT_FOUND, HTTPResponseFailure.empty())
                    .and()
                .and()
                .build(PetsApi.class, "http://localhost:7070");

        Either<HTTPResponseException, Pet> puka = petsApi.create(new NewPet("Puka", 2));

        puka.peek(pet -> System.out.println("a new pet was created: " + pet + "..."))
            .peekLeft(HTTPResponseException::printStackTrace);

        puka.toOption()
                .flatMap(pet -> petsApi.read(pet.id))
                .onEmpty(() -> System.out.println("the pet was not found..."))
                .peek(pet -> System.out.println("the pet was found: " + pet + "..."));

        System.out.println("All pets: " + petsApi.readAllPets()
                .collect(Collectors.mapping(Pet::toString, Collectors.joining(","))));

        petsApi.read(999)
                .onEmpty(() -> System.out.println("the pet was not found..."))
                .peek(pet -> System.out.println("the pet was found: " + pet + "..."));

        puka.toTry()
                // first update the pet...
                .flatMap(pet -> petsApi.update(pet.id, new NewPet(pet.name, pet.age + 1)))
                .peek(response -> response.onSuccess(((status, headers, pet) -> System.out.println("the pet was updated: " + pet + "... and the HTTP status was " + status + "."))))
                .flatMap(response -> response.fold(Try::success, Try::failure))
                // and now, delete it
                .flatMap(pet -> petsApi.delete(pet.id))
                .peek(status -> System.out.println("the pet was deleted...and the HTTP status was " + status + "."))
                .onFailure(Throwable::printStackTrace);

        // now, we are going to see how we can recover from a failure response using an Either<MyErrorType, MySuccessType>
        // in case we send an invalid pet, we will receive a Bad Request response with a json explaining the problems
        petsApi.createOr(new NewPet("", 0))
                .peek(pet -> System.out.println("a new pet was created: " + pet + "..."))
                .peekLeft(problems -> System.out.println("there is some problems with our pet: " + problems + "..."));
    }

    @Path("/pets")
    interface PetsApi {

        @POST
        Either<HTTPResponseException, Pet> create(@JsonContent NewPet pet);

        @POST
        Either<Problems, Pet> createOr(@JsonContent NewPet pet);

        @GET("/{id}")
        Option<Pet> read(@Path int id);

        @GET("/")
        List<Pet> readAllPets(); // all main vavr collections/data structures are supported (List, Seq, Array, LinkedList, Stream, etc)

        @PUT("/{id}")
        Try<HTTPResponse<Pet>> update(@Path int id, @JsonContent NewPet pet);

        @DELETE("/{id}")
        Try<HTTPStatus> delete(@Path int id);
    }

    record NewPet(String name, int age) {}

    record Pet(int id, String name, int age) {}

    record Problems(Collection<Problem> problems) {}

    record Problem(String field, String message) {}
}


