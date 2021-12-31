package com.github.ljtfreitas.julian.samples;

import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.http.HTTPResponse;
import com.github.ljtfreitas.julian.http.HTTPStatusCode;
import com.github.ljtfreitas.julian.samples.PetsAPI.Pet;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;

import static com.github.ljtfreitas.julian.http.HTTPResponseFailure.empty;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class Program2 {

    public static void main(String[] args) throws InterruptedException {
        try (MockServerClient mockServerClient = petsMockServer(8090)) {
            PetsAPI pets = new ProxyBuilder()
                    .http()
                        .failure()
                            .when(HTTPStatusCode.NOT_FOUND, empty()) // when NOT FOUND => null
                                .and()
                            .and()
                    .build(PetsAPI.class, "http://localhost:" + mockServerClient.getPort());

            System.out.println("All pets: " + pets.allPets());

            System.out.println("One pets: " + pets.onePet(1));

            pets.onePetAsOptional(1)
                    .ifPresent(pet -> System.out.println("One pet as Optional, when present: " + pet));

            pets.onePetAsOptional(1234) // GET /pets/1234 => not found
                    .ifPresentOrElse(p -> {}, () -> System.out.println("Not Found."));

            pets.onePetAsFuture(1)
                    .thenAccept(pet -> System.out.println("One pet, async: " + pet));

            pets.onePetAsPromise(1)
                    .onSuccess(people -> System.out.println("One pet, async: " + people))
                    .then(Pet::name)
                    .onSuccess(people -> System.out.println("One pet's name, async: " + people))
                    .onFailure(e -> System.err.println("an error happened..." + e.getMessage()));

            pets.onePetAsExcept(1)
                    .onSuccess(people -> System.out.println("One pet, as Except: " + people))
                    .onFailure(e -> System.err.println("an error happened..." + e.getMessage()));

            HTTPResponse<Pet> response = pets.onePetAsHTTPResponse(1);
            System.out.println("One pet, as HTTP response...status is " + response.status() + " and headers are " + response.headers());
            response.onSuccess(pet -> System.out.println("One pet, as HTTP response body: " + pet))
                    .onFailure(e -> System.err.println("an error happened..." + e.getMessage()));

            pets.onePetAsAsyncHTTPResponse(1)
                    .thenAccept(r -> {
                        System.out.println("One pet, as async HTTP response...status is " + response.status() + " and headers are " + response.headers());

                        r.onSuccess(people -> System.out.println("One pet, as async HTTP response body: " + people))
                                .onFailure(e -> System.err.println("an error happened..." + e.getMessage()));
                    });

            Thread.sleep(1000);

            System.out.println("Creating a new pet: " + pets.newPet(new Pet("Zulu", "cat", 8)));
        }
    }

    static MockServerClient petsMockServer(int port) {
        ClientAndServer mockServer = new ClientAndServer(8090);

        mockServer.when(request("/pets")
                        .withMethod("GET"))
                .respond(response("""
                        [{"name": "Puka", "kind": "cat", "age": 1},
                        {"name": "Hugo", "kind": "cat", "age": 1}]
                        """)
                        .withHeader("Content-Type", "application/json")
                        .withStatusCode(200));

        mockServer.when(request("/pets/1")
                        .withMethod("GET"))
                .respond(response("""
                        {"name": "Puka", "kind": "cat", "age": 1},
                        """)
                        .withHeader("Content-Type", "application/json")
                        .withStatusCode(200));

        mockServer.when(request("/pets/1")
                        .withMethod("GET"))
                .respond(response("""
                        {"name": "Hugo", "kind": "cat", "age": 1},
                        """)
                        .withHeader("Content-Type", "application/json")
                        .withStatusCode(200));

        mockServer.when(request("/pets")
                        .withMethod("POST")
                        .withHeader("Content-Type", "application/json"))
                .respond(response().withStatusCode(201));

        return mockServer;
    }
}
