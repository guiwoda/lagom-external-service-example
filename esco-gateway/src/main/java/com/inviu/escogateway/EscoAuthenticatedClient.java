package com.inviu.escogateway;

import static java.util.concurrent.CompletableFuture.completedFuture;

import com.inviu.esco.api.EscoService;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import esco.api.front.v3.models.request.BoletosRequest;
import esco.api.front.v3.models.request.CtaCorrienteConsolidadaRequest;
import esco.api.front.v3.models.request.LoginRequest;
import esco.api.front.v3.models.response.BoletosItem;
import esco.api.front.v3.models.response.CtaCorrienteConsolidadoItem;
import esco.api.front.v3.models.response.LoginResponse;

import javax.inject.Inject;
import java.util.List;

/**
 * Wrapper around the EscoService client to force the authentication flow when needed.
 */
public class EscoAuthenticatedClient implements EscoService {
    private final EscoService escoService;
    private final AuthenticationRepository authenticationRepository;

    @Inject
    public EscoAuthenticatedClient(EscoService escoService, AuthenticationRepository authenticationRepository) {
        this.escoService = escoService;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public ServiceCall<CtaCorrienteConsolidadaRequest, List<CtaCorrienteConsolidadoItem>> getCtaCorrienteConsolidada() {
        return authenticated(escoService.getCtaCorrienteConsolidada());
    }

    @Override
    public ServiceCall<BoletosRequest, List<BoletosItem>> getBoletos() {
        return authenticated(escoService.getBoletos());
    }

    @Override
    public ServiceCall<LoginRequest, LoginResponse> login() {
        return escoService.login();
    }

    private <Request, Response> ServiceCall<Request, Response> authenticated(ServiceCall<Request, Response> serviceCall) {
        return authenticationRepository.getAccessToken()
                .thenCompose(accessToken -> {
                    if (!accessToken.isValid()) {
                        return escoService.login().invoke(credentials())
                                .thenCompose(loginResponse -> authenticationRepository.updateAccessToken(
                                        EscoAccessToken.of(
                                                loginResponse.accessToken(),
                                                EscoDate.parse(loginResponse.expires()).toLocalDateTime())));
                    }

                    return completedFuture(accessToken);
                }).thenApply(accessToken -> serviceCall.handleRequestHeader(header -> header
                        .withHeader("Authorization", "Bearer " + accessToken.token())))
                .toCompletableFuture()
                .join();
    }

    private LoginRequest credentials() {
        EscoCredentials credentials = authenticationRepository.getCredentials();
        return LoginRequest.of(credentials.userName(), credentials.password());
    }
}
