package com.inviu.escogateway;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.Adapter;
import akka.actor.typed.javadsl.AskPattern;
import com.inviu.escogateway.EscoAuth.Command;
import com.typesafe.config.Config;

import javax.inject.Inject;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class DistributedDataAuthenticationRepository implements AuthenticationRepository {
    private static final Duration askTimeout = Duration.ofSeconds(5);
    private final ActorRef<Command> escoAuth;
    private final Config config;
    private final ActorSystem<Void> actorSystem;

    @Inject
    public DistributedDataAuthenticationRepository(
            ActorRef<Command> escoAuth,
            Config config,
            akka.actor.ActorSystem actorSystem) {
        this.escoAuth = escoAuth;
        this.config = config;
        this.actorSystem = Adapter.toTyped(actorSystem);
    }

    @Override
    public CompletionStage<EscoAccessToken> getAccessToken() {
        return AskPattern.ask(
                escoAuth,
                GetCachedAuthInfo::of,
                askTimeout,
                actorSystem.scheduler())
                .thenApply(AuthInfoReply::accessToken);
    }

    @Override
    public CompletionStage<EscoAccessToken> updateAccessToken(EscoAccessToken accessToken) {
        return AskPattern.ask(
                escoAuth,
                (ActorRef<AuthInfoReply> replyTo) -> UpdateAuthInfo.of(replyTo, accessToken),
                askTimeout,
                actorSystem.scheduler())
                .thenApply(AuthInfoReply::accessToken);
    }

    @Override
    public EscoCredentials getCredentials() {
        return EscoCredentials.of(config.getString("esco.username"), config.getString("esco.password"));
    }
}
