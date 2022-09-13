package com.github.ljtfreitas.julian.samples.reactor;

import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.DELETE;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.JsonContent;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.PUT;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.http.HTTPResponse;
import com.github.ljtfreitas.julian.http.HTTPStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public class ReactorSamples {

    public static void main(String[] args) {
        PetsApi petsApi = new ProxyBuilder()
                .build(PetsApi.class, "http://localhost:7070");

        Pet puka = petsApi.create(new NewPet("Puka", 2))
                .doOnNext(pet -> System.out.println("a new pet was created: " + pet + "..."))
                .flatMap(pet -> petsApi.read(pet.id))
                .doOnNext(pet -> System.out.println("the pet was found: " + pet + "..."))
                .block();

        System.out.println("All pets: " + petsApi.readAllPets()
                .collect(Collectors.mapping(Pet::toString, Collectors.joining(",")))
                .block());

        petsApi.read(999)
                .subscribe(pet -> {}, Throwable::printStackTrace, () -> System.out.println("the pet was not found..."));

        petsApi.update(puka.id, new NewPet(puka.name, puka.age + 1))
                .flatMap(response -> Mono.justOrEmpty(response.body().op()))
                .doOnNext(pet -> System.out.println("the pet was updated: " + pet + "..."))
                .flatMap(pet -> petsApi.delete(pet.id)
                        .doOnNext(status -> System.out.println("the pet " + pet.id + " was deleted...and the HTTP status was " + status + ".")))
                .doOnError(Throwable::printStackTrace)
                .block();
    }

    @Path("/pets")
    interface PetsApi {

        @POST
        Mono<Pet> create(@JsonContent NewPet pet);

        @GET("/{id}")
        Mono<Pet> read(@Path int id);

        @GET("/")
        Flux<Pet> readAllPets();

        @PUT("/{id}")
        Mono<HTTPResponse<Pet>> update(@Path int id, @JsonContent NewPet pet);

        @DELETE("/{id}")
        Mono<HTTPStatus> delete(@Path int id);
    }

    record NewPet(String name, int age) {}

    record Pet(int id, String name, int age) {}

}


