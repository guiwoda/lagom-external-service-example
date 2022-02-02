package com.inviu.escogateway;

import static play.api.libs.concurrent.TypedAkka.actorRefOf;
import static play.api.libs.concurrent.TypedAkka.behaviorOf;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.inviu.esco.api.EscoService;
import com.inviu.escogateway.api.EscoGatewayService;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import play.api.libs.concurrent.AkkaGuiceSupport;
import play.api.libs.concurrent.TypedActorRefProvider;
import scala.reflect.ClassTag;

public class EscoGatewayModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindService(EscoGatewayService.class, EscoGatewayServiceImpl.class);
        bindClient(EscoService.class);
        bind(AuthenticationRepository.class).to(DistributedDataAuthenticationRepository.class).in(Singleton.class);
        bind(EscoAuthenticatedClient.class).in(Singleton.class);
        bindEscoAuth();
    }

    /**
     * Vaguely stolen from {@link AkkaGuiceSupport}.
     */
    private void bindEscoAuth() {
        Class<EscoAuth.Command> messageType = EscoAuth.Command.class;

        bind(behaviorOf(messageType)).toInstance(EscoAuth.create());
        bind(actorRefOf(messageType))
                .toProvider(new TypedActorRefProvider<>("esco-auth", ClassTag.apply(messageType)))
                .asEagerSingleton();
    }
}
