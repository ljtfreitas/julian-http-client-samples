package com.github.ljtfreitas.julian.samples.vavr;

import com.github.ljtfreitas.julian.contract.GET;
import com.github.ljtfreitas.julian.contract.Path;
import com.github.ljtfreitas.julian.http.HTTPResponseException;
import com.github.ljtfreitas.julian.http.HTTPStatus;
import io.vavr.control.Either;
import io.vavr.control.Try;

public interface HTTPBin {

    @GET("/status/{codes}")
    Try<String> statusAsTry(@Path("codes") String codes);

    @GET("/status/{codes}")
    Either<HTTPResponseException, String> statusAsEither(@Path("codes") String codes);
}
