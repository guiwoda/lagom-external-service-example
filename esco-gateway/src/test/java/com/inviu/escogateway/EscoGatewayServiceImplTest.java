package com.inviu.escogateway;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.bind;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.eventually;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.startServer;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inviu.esco.api.EscoService;
import com.inviu.escogateway.api.EscoGatewayService;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.testkit.ServiceTest;
import esco.api.front.v3.models.request.LoginRequest;
import esco.api.front.v3.models.response.LoginResponse;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import scala.concurrent.duration.FiniteDuration;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class EscoGatewayServiceImplTest {
    private static final AuthenticationRepository authenticationRepository = mock(AuthenticationRepository.class);
    private static final EscoService escoService = mock(EscoService.class);

    private static ServiceTest.TestServer server;

    @BeforeClass
    public static void serverSetup() {
        ServiceTest.Setup setup = defaultSetup()
                .withJdbc()
                .configureBuilder(builder -> builder.overrides(
                        bind(EscoService.class).toInstance(escoService),
                        bind(AuthenticationRepository.class).toInstance(authenticationRepository)
                ));

        server = startServer(setup);
    }

    @AfterClass
    public static void shutdownServer() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    @Before
    public void setup() {
        mockEscoService();
        mockAuthenticationRepository();
    }

    private void mockEscoService() {
        Mockito.reset(escoService);
        when(escoService.getBoletos()).thenReturn(
                request -> completedFuture(StubFactory.createTickets()));
    }

    public void mockAuthenticationRepository() {
        Mockito.reset(authenticationRepository);
        // Default to a cached valid token, login flow will be test separately.
        when(authenticationRepository.getAccessToken()).thenReturn(completedFuture(
                EscoAccessToken.of("1234567890ABCDEF", LocalDateTime.now().plusDays(50))));
        when(authenticationRepository.getCredentials()).thenReturn(
                EscoCredentials.of("fakeuser", "fakepw"));
        when(authenticationRepository.updateAccessToken(any())).thenAnswer(args ->
                completedFuture(args.getArgument(0)));
    }

    @Test
    public void itCallsLoginBeforeCallingEscoWithoutCredentials() {
        when(authenticationRepository.getAccessToken()).thenReturn(completedFuture(EscoAccessToken.EMPTY));
        when(escoService.login()).then(args -> {
            LoginResponse loginResponse = StubFactory.createLoginResponse();

            return (ServiceCall<LoginRequest, LoginResponse>) request -> completedFuture(loginResponse);
        });

        when(authenticationRepository.updateAccessToken(any())).then(args -> {
            EscoAccessToken accessToken = args.getArgument(0);
            // Override access token mock after login was called on ESCO
            CompletableFuture<EscoAccessToken> out = completedFuture(accessToken);
            when(authenticationRepository.getAccessToken()).thenReturn(out);
            return out;
        });

        EscoGatewayService client = server.client(EscoGatewayService.class);
        client.getTickets()
                .invoke()
                .toCompletableFuture()
                .join();

        verify(escoService).login();
    }

    @Test
    public void itCallsLoginBeforeCallingEscoWithExpiredCredentials() {
        when(authenticationRepository.getAccessToken()).thenReturn(completedFuture(
                EscoAccessToken.of("avalidtoken", LocalDateTime.now().minusSeconds(1))));

        when(escoService.login()).then(args -> {
            LoginResponse loginResponse = StubFactory.createLoginResponse();
            return (ServiceCall<LoginRequest, LoginResponse>) request -> completedFuture(loginResponse);
        });

        when(authenticationRepository.updateAccessToken(any())).then(args -> {
            EscoAccessToken accessToken = args.getArgument(0);
            // Override access token mock after login was called on ESCO
            CompletableFuture<EscoAccessToken> out = completedFuture(accessToken);
            when(authenticationRepository.getAccessToken()).thenReturn(out);
            return out;
        });

        EscoGatewayService client = server.client(EscoGatewayService.class);
        client.getTickets()
                .invoke()
                .toCompletableFuture()
                .join();

        eventually(new FiniteDuration(5, TimeUnit.SECONDS), () -> {
            verify(escoService).login();
            verify(authenticationRepository).updateAccessToken(any());
        });
    }
}
