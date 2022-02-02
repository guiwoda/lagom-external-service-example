package esco.api.front.v3.models.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;
import org.immutables.value.Value.Redacted;

@Immutable(builder = false)
@ImmutableStyle
@JsonDeserialize(as = LoginRequest.class)
public interface AbstractLoginRequest {
    @Parameter
    String userName();

    @Redacted
    @Parameter
    String password();
}
