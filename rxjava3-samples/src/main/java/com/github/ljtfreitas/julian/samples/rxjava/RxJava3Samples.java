package com.github.ljtfreitas.julian.samples.rxjava;

import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.DELETE;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.JsonContent;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.PUT;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.http.HTTPResponse;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import java.util.stream.Collectors;

public class RxJava3Samples {

    public static void main(String[] args) {
        PetsApi petsApi = new ProxyBuilder()
                .build(PetsApi.class, "http://localhost:7070");

        Pet puka = petsApi.create(new NewPet("Puka", 2))
                .doOnSuccess(pet -> System.out.println("a new pet was created: " + pet + "..."))
                .flatMapMaybe(pet -> petsApi.read(pet.id))
                .doOnSuccess(pet -> System.out.println("the pet was found: " + pet + "..."))
                .blockingGet();

        System.out.println("All pets: " + petsApi.readAllPets()
                .collect(Collectors.mapping(Pet::toString, Collectors.joining(",")))
                .blockingGet());

        petsApi.read(999)
                .subscribe(pet -> {}, Throwable::printStackTrace, () -> System.out.println("the pet was not found..."));

        petsApi.update(puka.id, new NewPet(puka.name, puka.age + 1))
                .flatMapMaybe(response -> Maybe.fromOptional(response.body().op()))
                .doOnSuccess(pet -> System.out.println("the pet was updated: " + pet + "..."))
                .flatMapCompletable(pet -> petsApi.delete(pet.id)
                        .doOnComplete(() -> System.out.println("the pet " + pet.id + " was deleted...")))
                .doOnError(Throwable::printStackTrace)
                .blockingAwait();
    }

    @Path("/pets")
    interface PetsApi {

        @POST
        Single<Pet> create(@JsonContent NewPet pet);

        @GET("/{id}")
        Maybe<Pet> read(@Path int id);

        @GET("/")
        Observable<Pet> readAllPets();

        @PUT("/{id}")
        Single<HTTPResponse<Pet>> update(@Path int id, @JsonContent NewPet pet);

        @DELETE("/{id}")
        Completable delete(@Path int id);
    }

    record NewPet(String name, int age) {}

    record Pet(int id, String name, int age) {}

}


