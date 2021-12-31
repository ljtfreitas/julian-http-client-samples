package com.github.ljtfreitas.julian.samples;

import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.http.HTTPResponse;
import com.github.ljtfreitas.julian.http.HTTPStatusCode;
import com.github.ljtfreitas.julian.samples.StarWarsAPI.Person;

import static com.github.ljtfreitas.julian.http.HTTPResponseFailure.empty;

public class Program {

    public static void main(String[] args) throws InterruptedException {
        StarWarsAPI starWars = new ProxyBuilder()
                .http()
                    .failure()
                        .when(HTTPStatusCode.NOT_FOUND, empty()) // when NOT FOUND => null
                        .and()
                    .and()
                .build(StarWarsAPI.class, "https://swapi.dev");

        System.out.println("All people: " + starWars.allPeople());

        starWars.allPeopleUsingCallback(allPeople -> System.out.println("All people, using callback: " + allPeople));

        System.out.println("One person: " + starWars.onePeople(1));

        starWars.onePeopleUsingCallback(1, person -> System.out.println("One person, using callback: " + person));

        starWars.onePeopleAsOptional(1)
                .ifPresent(person -> System.out.println("One person as Optional, when present: " + person));

        starWars.onePeopleAsOptional(1234) // GET /people/1234 => not found
                .ifPresentOrElse(p -> {}, () -> System.out.println("Not Found."));

        starWars.onePeopleAsFuture(1)
                .thenAccept(person -> System.out.println("One person, async: " + person));

        starWars.onePeopleAsPromise(1)
                .onSuccess(person -> System.out.println("One person, async: " + person))
                .then(Person::name)
                .onSuccess(person -> System.out.println("One person's name, async: " + person))
                .onFailure(e -> System.err.println("an error happened..." + e.getMessage()));

        starWars.onePeopleAsExcept(1)
                .onSuccess(person -> System.out.println("One person, as Except: " + person))
                .onFailure(e -> System.err.println("an error happened..." + e.getMessage()));

        HTTPResponse<Person> response = starWars.onePeopleAsHTTPResponse(1);
        System.out.println("One person, as HTTP response...status is " + response.status() + " and headers are " + response.headers());
        response.onSuccess(person -> System.out.println("One person, as HTTP response body: " + person))
                .onFailure(e -> System.err.println("an error happened..." + e.getMessage()));

        starWars.onePeopleAsAsyncHTTPResponse(1)
                .thenAccept(r -> {
                    System.out.println("One person, as async HTTP response...status is " + response.status() + " and headers are " + response.headers());

                    r.onSuccess(person -> System.out.println("One person, as async HTTP response body: " + person))
                            .onFailure(e -> System.err.println("an error happened..." + e.getMessage()));
                });

        Thread.sleep(1000);
    }
}
