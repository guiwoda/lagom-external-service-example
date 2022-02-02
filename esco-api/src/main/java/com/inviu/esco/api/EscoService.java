package com.inviu.esco.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;

import com.lightbend.lagom.javadsl.api.CircuitBreaker;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;
import com.typesafe.config.ConfigFactory;
import esco.api.front.v3.models.request.BoletosRequest;
import esco.api.front.v3.models.request.CtaCorrienteConsolidadaRequest;
import esco.api.front.v3.models.request.LoginRequest;
import esco.api.front.v3.models.response.BoletosItem;
import esco.api.front.v3.models.response.CtaCorrienteConsolidadoItem;
import esco.api.front.v3.models.response.LoginResponse;

import java.util.List;

public interface EscoService extends Service {
    String API_PREFIX = "/api/v";
    String ESCO = "esco";

    ServiceCall<CtaCorrienteConsolidadaRequest, List<CtaCorrienteConsolidadoItem>> getCtaCorrienteConsolidada();
    ServiceCall<BoletosRequest, List<BoletosItem>> getBoletos();
    ServiceCall<LoginRequest, LoginResponse> login();

    default Descriptor descriptor() {
        String apiVersion = ConfigFactory.load().getString("esco.api-version");
        return named(ESCO)
                .withCalls(
                        restCall(Method.POST, API_PREFIX + apiVersion + "/get-ctaCorriente-consolidada", this::getCtaCorrienteConsolidada),
                        restCall(Method.POST, API_PREFIX + apiVersion + "/get-boletos", this::getBoletos),
                        restCall(Method.POST, API_PREFIX + apiVersion + "/login", this::login)
                )
                .withHeaderFilter(new ApiVersionHeaderFilter(apiVersion))
                .withCircuitBreaker(CircuitBreaker.identifiedBy(ESCO))
                .withAutoAcl(true);
    }
}
