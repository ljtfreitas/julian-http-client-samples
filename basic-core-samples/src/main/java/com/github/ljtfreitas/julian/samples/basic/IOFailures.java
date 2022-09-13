package com.github.ljtfreitas.julian.samples.basic;

import com.github.ljtfreitas.julian.Promise;
import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.http.HTTPClientFailureResponseException;
import com.github.ljtfreitas.julian.http.HTTPException;
import com.github.ljtfreitas.julian.http.HTTPResponseException;
import com.github.ljtfreitas.julian.http.HTTPServerFailureResponseException;
import com.github.ljtfreitas.julian.http.client.HTTPClientException;

class IOFailures {

    public static void main(String[] args) {
        HTTPBin httpBin = new ProxyBuilder()
                .build(HTTPBin.class, "https://wrong-api-address.com");

        // we can use Promise to handle (and recover from) errors
        Promise<String> promise = httpBin.getAsPromise();

        promise.onFailure(Throwable::printStackTrace)
                .onSuccess(System.out::println);
        System.out.println("#############");

        promise.recover(e -> "some fallback value...")
                .onSuccess(System.out::println);
        System.out.println("#############");

        // we can map the exception as well
        promise.failure(MyException::new)
                .onFailure(Throwable::printStackTrace);
        System.out.println("#############");

        // Promise.join get an Attempt<T> value
        promise.onFailure(Throwable::printStackTrace)
                .onSuccess(System.out::println);

        // for sync requests, we can use an usual try/catch block
        try {
            String body = httpBin.get();
            System.out.println("The response body: " + body);

        } catch (HTTPClientFailureResponseException e) { // any 4xx responses
            e.printStackTrace();

        } catch (HTTPServerFailureResponseException e) { // any 5xx responses
            e.printStackTrace();

        } catch (HTTPResponseException e) { // any http failed response (4xx or 5xx)
            e.printStackTrace();

        } catch (HTTPClientException e) { // IO/serialization/deserialization errors
            e.printStackTrace();

        } catch (HTTPException e) { // the highest julian-http-client exception
            e.printStackTrace();
        }

        System.out.println("#############");
    }

    static class MyException extends RuntimeException {

        MyException(Throwable cause) {
            super(cause);
        }
    }
}
