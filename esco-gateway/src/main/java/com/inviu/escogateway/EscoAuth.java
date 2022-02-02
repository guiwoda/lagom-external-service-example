package com.inviu.escogateway;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.cluster.ddata.Key;
import akka.cluster.ddata.LWWRegister;
import akka.cluster.ddata.LWWRegisterKey;
import akka.cluster.ddata.SelfUniqueAddress;
import akka.cluster.ddata.typed.javadsl.DistributedData;
import akka.cluster.ddata.typed.javadsl.Replicator;
import akka.cluster.ddata.typed.javadsl.Replicator.Get;
import akka.cluster.ddata.typed.javadsl.Replicator.GetResponse;
import akka.cluster.ddata.typed.javadsl.Replicator.GetSuccess;
import akka.cluster.ddata.typed.javadsl.Replicator.Update;
import akka.cluster.ddata.typed.javadsl.Replicator.UpdateResponse;
import akka.cluster.ddata.typed.javadsl.Replicator.UpdateSuccess;
import akka.cluster.ddata.typed.javadsl.ReplicatorMessageAdapter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.inviu.escogateway.EscoAuth.Command;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.serialization.CompressedJsonable;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

public class EscoAuth extends AbstractBehavior<Command> {
    /**
     * Identifier for the shared state of the ESCO auth information.
     */
    private static final String DISTRIBUTED_AUTH_INFO_KEY = "ESCO_AUTH";

    interface Command extends CompressedJsonable {}

    @Immutable(builder = false)
    @ImmutableStyle
    @JsonDeserialize(as = GetAuthInfo.class)
    interface AbstractGetAuthInfo extends Command {
        @Parameter
        ActorRef<AuthInfoReply> replyTo();
    }

    @Immutable(builder = false)
    @ImmutableStyle
    @JsonDeserialize(as = GetCachedAuthInfo.class)
    interface AbstractGetCachedAuthInfo extends Command {
        @Parameter
        ActorRef<AuthInfoReply> replyTo();
    }

    @Immutable(builder = false)
    @ImmutableStyle
    @JsonDeserialize(as = AuthInfoReply.class)
    interface AbstractAuthInfoReply extends CompressedJsonable {
        @Parameter
        EscoAccessToken accessToken();
        default boolean isValid() {
            return accessToken().isValid();
        }
    }

    @Immutable(builder = false)
    @ImmutableStyle
    @JsonDeserialize(as = UpdateAuthInfo.class)
    interface AbstractUpdateAuthInfo extends Command {
        @Parameter
        ActorRef<AuthInfoReply> replyTo();
        @Parameter
        EscoAccessToken accessToken();
    }

    @Immutable
    @ImmutableStyle
    @JsonDeserialize(as = InternalUpdateReply.class)
    interface AbstractInternalUpdateReply extends Command {
        ActorRef<AuthInfoReply> replyTo();
        UpdateResponse<LWWRegister<AuthInfoReply>> response();
        AuthInfoReply newValue();
    }

    @Immutable(builder = false)
    @ImmutableStyle
    interface AbstractInternalGetReply extends Command {
        @Parameter
        ActorRef<AuthInfoReply> replyTo();
        @Parameter
        GetResponse<LWWRegister<AuthInfoReply>> response();
    }

    private final ReplicatorMessageAdapter<Command, LWWRegister<AuthInfoReply>> replicatorAdapter;

    private final SelfUniqueAddress node;
    private final Key<LWWRegister<AuthInfoReply>> key;
    private AuthInfoReply cached = AuthInfoReply.of(EscoAccessToken.EMPTY);

    EscoAuth(
            ActorContext<Command> context,
            ReplicatorMessageAdapter<Command, LWWRegister<AuthInfoReply>> replicatorAdapter,
            Key<LWWRegister<AuthInfoReply>> key,
            SelfUniqueAddress node) {
        super(context);

        this.replicatorAdapter = replicatorAdapter;
        this.node = node;
        this.key = key;
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(ctx -> DistributedData.withReplicatorMessageAdapter(
                (ReplicatorMessageAdapter<Command, LWWRegister<AuthInfoReply>> replicatorAdapter) -> new EscoAuth(
                        ctx,
                        replicatorAdapter,
                        LWWRegisterKey.create(DISTRIBUTED_AUTH_INFO_KEY),
                        DistributedData.get(ctx.getSystem()).selfUniqueAddress())
        ));
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(GetCachedAuthInfo.class, this::onGetCachedAuthInfo)
                .onMessage(GetAuthInfo.class, this::onGetAuthInfo)
                .onMessage(UpdateAuthInfo.class, this::onUpdateAuthInfo)
                .onMessage(InternalGetReply.class, this::onInternalGetReply)
                .onMessage(InternalUpdateReply.class, this::onInternalUpdateReply)
                .build();
    }

    private Behavior<Command> onGetCachedAuthInfo(GetCachedAuthInfo cmd) {
        cmd.replyTo().tell(cached);
        return Behaviors.same();
    }

    private Behavior<Command> onGetAuthInfo(GetAuthInfo cmd) {
        replicatorAdapter.askGet(
                replyTo -> new Get<>(key, Replicator.readLocal(), replyTo),
                response -> InternalGetReply.of(cmd.replyTo(), response));

        return Behaviors.same();
    }

    private Behavior<Command> onUpdateAuthInfo(UpdateAuthInfo cmd) {
        AuthInfoReply newValue = AuthInfoReply.of(cmd.accessToken());

        replicatorAdapter.askUpdate(
                askReplyTo -> new Update<>(
                        key,
                        LWWRegister.create(node, cached),
                        Replicator.writeLocal(),
                        askReplyTo,
                        current -> current.withValue(node, newValue)),
                response -> InternalUpdateReply.builder()
                        .replyTo(cmd.replyTo())
                        .response(response)
                        .newValue(newValue)
                        .build());

        return Behaviors.same();
    }

    private Behavior<Command> onInternalGetReply(InternalGetReply cmd) {
        GetResponse<LWWRegister<AuthInfoReply>> response = cmd.response();

        if (response instanceof GetSuccess) {
            GetSuccess<LWWRegister<AuthInfoReply>> success = (GetSuccess<LWWRegister<AuthInfoReply>>) response;
            cached = success.get(success.key()).value();
        }

        cmd.replyTo().tell(cached);

        return Behaviors.same();
    }

    private Behavior<Command> onInternalUpdateReply(InternalUpdateReply cmd) {
        if (cmd.response() instanceof UpdateSuccess) {
            cached = cmd.newValue();
        }

        cmd.replyTo().tell(cached);

        return Behaviors.same();
    }
}
