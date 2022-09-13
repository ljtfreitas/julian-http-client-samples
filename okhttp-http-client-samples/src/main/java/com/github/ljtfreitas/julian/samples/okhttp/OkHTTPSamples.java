package com.github.ljtfreitas.julian.samples.okhttp;

import com.github.ljtfreitas.julian.Promise;
import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.Response;
import com.github.ljtfreitas.julian.contract.DELETE;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.JsonContent;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.PUT;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.http.HTTPResponse;
import com.github.ljtfreitas.julian.http.HTTPResponseFailure;
import com.github.ljtfreitas.julian.http.HTTPStatus;
import com.github.ljtfreitas.julian.http.HTTPStatusCode;
import com.github.ljtfreitas.julian.http.client.okhttp.OkHTTPClient;

import java.util.Optional;

import static com.github.ljtfreitas.julian.http.HTTPResponseFailure.empty;

public class OkHTTPSamples {

    public static void main(String[] args) {
        try (OkHTTPClient okHTTPClient = new OkHTTPClient()) {
            PetsApi petsApi = new ProxyBuilder()
                    .http()
                        .client()
                            .with(okHTTPClient)
                        .failure()
                            .when(HTTPStatusCode.NOT_FOUND, empty())
                        .and()
                    .and()
                    .build(PetsApi.class, "http://localhost:7070");

            petsApi.create(new NewPet("Puka", 2))
                    .onSuccess(pet -> System.out.println("a new pet was created: " + pet + "..."))
                    .bind(pet -> petsApi.read(pet.id))
                    .then(Optional::orElseThrow)
                    .onSuccess(pet -> System.out.println("the pet was found: " + pet + "..."))
                    .bind(pet -> petsApi.update(pet.id, new NewPet(pet.name, pet.age + 1)))
                    .bind(Response::promise)
                    .onSuccess(pet -> System.out.println("the pet was updated: " + pet + "..."))
                    .bind(pet -> petsApi.delete(pet.id)
                        .onSuccess(status -> System.out.println("the pet " + pet.id + " was deleted...and the HTTP status was " + status + ".")))
                    .join()
                    .onFailure(Throwable::printStackTrace);
        }
    }

    @Path("/pets")
    interface PetsApi {

        @POST
        Promise<Pet> create(@JsonContent NewPet pet);

        @GET("/{id}")
        Promise<Optional<Pet>> read(@Path int id);

        @PUT("/{id}")
        Promise<HTTPResponse<Pet>> update(@Path int id, @JsonContent NewPet pet);

        @DELETE("/{id}")
        Promise<HTTPStatus> delete(@Path int id);
    }

    record NewPet(String name, int age) {}

    record Pet(int id, String name, int age) {}

}


