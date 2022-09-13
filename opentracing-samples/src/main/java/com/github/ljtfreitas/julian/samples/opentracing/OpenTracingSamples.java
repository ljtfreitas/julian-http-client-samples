package com.github.ljtfreitas.julian.samples.opentracing;

import com.github.ljtfreitas.julian.ProxyBuilder;
import com.github.ljtfreitas.julian.QueryParameters;
import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.JsonContent;
import com.github.ljtfreitas.julian.contract.POST;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.http.opentracing.TracingHTTPRequestInterceptor;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.reporters.RemoteReporter;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.jaegertracing.spi.Reporter;
import io.jaegertracing.spi.Sender;
import io.jaegertracing.thrift.internal.senders.HttpSender;
import io.opentracing.Tracer;
import org.apache.thrift.transport.TTransportException;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

public class OpenTracingSamples {

    public static void main(String[] args) throws TTransportException, InterruptedException {
        try (JaegerAllInOneContainer jaegerContainer = new JaegerAllInOneContainer().init()) {
            jaegerContainer.start();

            JaegerTracer tracer = jaegerContainer.tracer();

            PetsApi petsApi = new ProxyBuilder()
                    .http()
                        .client()
                            .extensions()
                                .debug()
                                    .enabled() // enable HTTP request/response logging; check to see opentracing headers
                                .and()
                            .and()
                        .and()
                        .interceptors()
                            .add(new TracingHTTPRequestInterceptor(tracer))
                        .and()
                    .and()
                    .build(PetsApi.class, "http://localhost:7070");

            Pet puka = petsApi.create(new NewPet("Puka", 2));
            System.out.println("a new pet was created: " + puka + "...");

            System.out.println("#########");

            Pet pet = petsApi.read(puka.id);
            System.out.println("the pet was found: " + pet + "...");

            System.out.println("#########");

            System.out.println("Now, we can see the traces at: http://localhost:" + jaegerContainer.queryPort() + "/search");

            Thread.sleep(60000);

            jaegerContainer.stop();
        }
    }

    @Path("/pets")
    interface PetsApi {

        @POST
        Pet create(@JsonContent NewPet pet);

        @GET("/{id}")
        Pet read(@Path int id);
    }

    record NewPet(String name, int age) {}

    record Pet(int id, String name, int age) {}

    private static class JaegerAllInOneContainer extends GenericContainer<JaegerAllInOneContainer> {

        private static final int JAEGER_QUERY_PORT = 16686;
        private static final int JAEGER_COLLECTOR_THRIFT_PORT = 14268;
        private static final int JAEGER_ADMIN_PORT = 14269;
        private static final int ZIPKIN_PORT = 9411;

        JaegerAllInOneContainer() {
            super("jaegertracing/all-in-one:latest");
        }

        JaegerTracer tracer() throws TTransportException {
            String endpoint = "http://localhost:" + collectorThriftPort() + "/api/traces";

            Sender sender = new HttpSender.Builder(endpoint).build();
            Reporter reporter = new RemoteReporter.Builder()
                    .withSender(sender)
                    .withFlushInterval(500)
                    .build();

            JaegerTracer.Builder tracerBuilder = new JaegerTracer.Builder("julian-http-client")
                    .withSampler(new ConstSampler(true))
                    .withReporter(reporter);

            return tracerBuilder.build();
        }

        int collectorThriftPort() {
            return getMappedPort(JAEGER_COLLECTOR_THRIFT_PORT);
        }

        JaegerAllInOneContainer init() {
            waitingFor(new BoundPortHttpWaitStrategy(JAEGER_ADMIN_PORT));

            withEnv("COLLECTOR_ZIPKIN_HOST_PORT", String.valueOf(ZIPKIN_PORT));

            withExposedPorts(
                    JAEGER_ADMIN_PORT,
                    JAEGER_COLLECTOR_THRIFT_PORT,
                    JAEGER_QUERY_PORT,
                    ZIPKIN_PORT
            );

            return this;
        }

        URI traces() {
            String parameters = new QueryParameters()
                    .join("service", "julian-http-client")
                    .serialize();

            return URI.create("http://localhost:" + queryPort() + "/api/traces?" + parameters);
        }

        int queryPort() {
            return getMappedPort(JAEGER_QUERY_PORT);
        }

        private static class BoundPortHttpWaitStrategy extends HttpWaitStrategy {

            private final int port;

            public BoundPortHttpWaitStrategy(int port) {
                this.port = port;
            }

            @Override
            protected Set<Integer> getLivenessCheckPorts() {
                return Collections.singleton(waitStrategyTarget.getMappedPort(port));
            }
        }
    }
}


