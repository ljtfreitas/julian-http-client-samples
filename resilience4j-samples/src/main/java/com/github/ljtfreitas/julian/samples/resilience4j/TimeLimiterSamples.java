package com.github.ljtfreitas.julian.samples.resilience4j;

import com.github.ljtfreitas.julian.Attempt;
import com.github.ljtfreitas.julian.Headers;
import com.github.ljtfreitas.julian.Promise;
import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.contract.Callback;
import com.github.ljtfreitas.julian.contract.DELETE;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.Header;
import com.github.ljtfreitas.julian.contract.JsonContent;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.PUT;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.contract.TextPlainContent;
import com.github.ljtfreitas.julian.http.HTTPHeaders;
import com.github.ljtfreitas.julian.http.HTTPResponse;
import com.github.ljtfreitas.julian.http.HTTPStatus;
import com.github.ljtfreitas.julian.http.resilience4j.TimeLimiterHTTPRequestInterceptor;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

public class TimeLimiterSamples {

    public static void main(String[] args) throws InterruptedException {
        TimeLimiter timeLimiter = TimeLimiter.of("my-time-limiter", TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofMillis(2000)) // 2 seconds timeout
                .build());

        // The scheduler is needed to schedule a timeout in a non-blocking way
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        HTTPBin httpBin = new ProxyBuilder()
                .http()
                    .interceptors()
                        .add(new TimeLimiterHTTPRequestInterceptor(timeLimiter, scheduler))
                    .and()
                .and()
                .build(HTTPBin.class, "https://httpbin.org");

        httpBin.delay(5) // 5 seconds delay
                .onSuccess(System.out::println)
                .onFailure(Throwable::printStackTrace); // timeout failure is expected here

        scheduler.shutdown();
    }

    interface HTTPBin {

        @GET("/delay/{delay}")
        Attempt<String> delay(@Path int delay);
    }
}


