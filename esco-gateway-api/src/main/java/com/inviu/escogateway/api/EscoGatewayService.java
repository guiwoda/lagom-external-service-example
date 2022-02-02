package com.inviu.escogateway.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;
import esco.api.front.v3.models.response.BoletosItem;

import java.util.List;

public interface EscoGatewayService extends Service {
    ServiceCall<NotUsed, List<BoletosItem>> getTickets();

    default Descriptor descriptor() {
        return named("EscoGateway")
                .withCalls(restCall(Method.GET, "/api/esco/tickets", this::getTickets))
                .withAutoAcl(true);
    }
}
