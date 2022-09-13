package com.github.ljtfreitas.julian.samples.gson;

import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.JsonContent;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.Path;
import com.google.gson.JsonObject;

class JsonGsonSamples {

    public static void main(String[] args) throws InterruptedException {
        // Gson codecs will be automatically registered
        PetsApi petsApi = new ProxyBuilder()
                .build(PetsApi.class, "http://localhost:7070");

        JsonObject responseAsJson = petsApi.create(new NewPet("Hugo", 2));

        System.out.println("POST: " + responseAsJson);
        System.out.println("#############");

        System.out.println("GET: " + petsApi.read(responseAsJson.get("id").getAsInt()));
        System.out.println("#############");
    }

    @Path("/pets")
    interface PetsApi {

        @POST
        JsonObject create(@JsonContent NewPet pet);

        @GET("/{id}")
        Pet read(@Path int id);
    }

    static class NewPet {

        final String name;
        final int age;

        NewPet(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    static class Pet {

        final int id;
        final String name;
        final int age;

        Pet(int id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Pet{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

}
