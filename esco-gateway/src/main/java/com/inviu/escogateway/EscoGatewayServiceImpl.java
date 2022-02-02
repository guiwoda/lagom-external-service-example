package com.inviu.escogateway;

import akka.NotUsed;
import com.inviu.escogateway.api.EscoGatewayService;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import esco.api.front.v3.models.request.BoletosRequest;
import esco.api.front.v3.models.response.BoletosItem;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public class EscoGatewayServiceImpl implements EscoGatewayService {
    private final PersistentEntityRegistry persistentEntityRegistry;
    private final EscoAuthenticatedClient escoService;

    @Inject
    public EscoGatewayServiceImpl(PersistentEntityRegistry persistentEntityRegistry, EscoAuthenticatedClient escoService) {
        this.persistentEntityRegistry = persistentEntityRegistry;
        this.escoService = escoService;
    }

    @Override
    public ServiceCall<NotUsed, List<BoletosItem>> getTickets() {
        return request -> escoService.getBoletos().invoke(getBoletosRequest(
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now(), Set.of()
        ));
    }

    private BoletosRequest getBoletosRequest(ZonedDateTime from, ZonedDateTime to, Set<String> accounts) {
        BoletosRequest.Builder builder = BoletosRequest.builder()
                .fechaDesde(from.toLocalDateTime())
                .fechaHasta(to.toLocalDateTime())
                .porConcertacion(true)
                .incluyeAnulados(true)
                .esConsolidado(!accounts.isEmpty());

        if (!accounts.isEmpty()) {
            builder.cuentas(String.join(",", accounts));
        }

        return builder.build();
    }
}
