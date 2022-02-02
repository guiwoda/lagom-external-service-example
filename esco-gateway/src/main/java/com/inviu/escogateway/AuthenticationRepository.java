package com.inviu.escogateway;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import java.time.LocalDateTime;
import java.util.concurrent.CompletionStage;

public interface AuthenticationRepository {
    @Immutable(builder = false)
    @ImmutableStyle
    @JsonDeserialize(as = EscoAccessToken.class)
    interface AbstractEscoAccessToken extends Jsonable {
        EscoAccessToken EMPTY = EscoAccessToken.of("", LocalDateTime.MIN);

        @Parameter
        String token();
        @Parameter
        LocalDateTime expiringDate();

        default boolean isValid() {
            LocalDateTime expires = expiringDate();
            return !equals(EMPTY) && expires != null && expires.isAfter(LocalDateTime.now());
        }
    }

    @Immutable(builder = false)
    @ImmutableStyle
    @JsonDeserialize(as = EscoCredentials.class)
    interface AbstractEscoCredentials extends Jsonable {
        @Parameter
        String userName();
        @Parameter
        String password();
    }

    CompletionStage<EscoAccessToken> getAccessToken();
    CompletionStage<EscoAccessToken> updateAccessToken(EscoAccessToken accessToken);
    EscoCredentials getCredentials();
}
