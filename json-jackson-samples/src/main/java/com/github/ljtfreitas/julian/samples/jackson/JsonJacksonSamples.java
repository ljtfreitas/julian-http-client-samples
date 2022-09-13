package com.github.ljtfreitas.julian.samples.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.JsonContent;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.Path;

import java.util.Collection;

class JsonJacksonSamples {

    public static void main(String[] args) throws InterruptedException {
        // Jackson codecs will be automatically registered

        PetsApi petsApi = new ProxyBuilder()
                .build(PetsApi.class, "http://localhost:7070");

        JsonNode responseAsJson = petsApi.create(new NewPet("Hugo", 2));

        System.out.println("POST: " + responseAsJson.toPrettyString());
        System.out.println("#############");

        System.out.println("GET: " + petsApi.read(responseAsJson.get("id").asInt()));
        System.out.println("#############");

        System.out.println("#############");
    }

    @Path("/pets")
    interface PetsApi {

        @POST
        JsonNode create(@JsonContent NewPet pet);

        @GET("/{id}")
        Pet read(@Path int id);
    }

    record NewPet(String name, int age) {}

    record Pet(int id, String name, int age) {}

}
