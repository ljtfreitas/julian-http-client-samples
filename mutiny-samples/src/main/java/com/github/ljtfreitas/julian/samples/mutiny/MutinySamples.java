package com.github.ljtfreitas.julian.samples.mutiny;

import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.DELETE;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.JsonContent;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.PUT;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.http.HTTPResponse;
import com.github.ljtfreitas.julian.http.HTTPStatus;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.stream.Collectors;

public class MutinySamples {

    public static void main(String[] args) {
        PetsApi petsApi = new ProxyBuilder()
                .build(PetsApi.class, "http://localhost:7070");

        Pet puka = petsApi.create(new NewPet("Puka", 2))
                .onItem()
                    .invoke(pet -> System.out.println("a new pet was created: " + pet + "..."))
                .flatMap(pet -> petsApi.read(pet.id))
                .onItem()
                    .invoke(pet -> System.out.println("the pet was found: " + pet + "..."))
                .await().indefinitely();

        System.out.println("All pets: " + petsApi.readAllPets()
                .collect()
                        .with(Collectors.mapping(Pet::toString, Collectors.joining(",")))
                .await().indefinitely());

        petsApi.read(999)
                .subscribe()
                    .with(pet -> System.out.println("the pet was found: " + pet + "..."), Throwable::printStackTrace);

        petsApi.update(puka.id, new NewPet(puka.name, puka.age + 1))
                .flatMap(response -> Uni.createFrom().optional(response.body().op()))
                .onItem()
                    .invoke(pet -> System.out.println("the pet was updated: " + pet + "..."))
                .flatMap(pet -> petsApi.delete(pet.id)
                    .onItem()
                        .invoke(status -> System.out.println("the pet " + pet.id + " was deleted...and the HTTP status was " + status + ".")))
                .onFailure()
                    .invoke(Throwable::printStackTrace)
                .await().indefinitely();
    }

    @Path("/pets")
    interface PetsApi {

        @POST
        Uni<Pet> create(@JsonContent NewPet pet);

        @GET("/{id}")
        Uni<Pet> read(@Path int id);

        @GET("/")
        Multi<Pet> readAllPets();

        @PUT("/{id}")
        Uni<HTTPResponse<Pet>> update(@Path int id, @JsonContent NewPet pet);

        @DELETE("/{id}")
        Uni<HTTPStatus> delete(@Path int id);
    }

    record NewPet(String name, int age) {}

    record Pet(int id, String name, int age) {}

}


