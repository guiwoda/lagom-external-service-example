package esco.api.front.v3.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value.Immutable;

@Immutable
@ImmutableStyle
@JsonDeserialize(as = LoginResponse.class)
public interface AbstractLoginResponse {
    @JsonProperty("access_token")
    String accessToken();
    @JsonProperty("token_type")
    String tokenType();
    @JsonProperty("expires_in")
    int expiresIn();
    @JsonProperty("refresh_token")
    String refreshToken();
    @JsonProperty(".issued")
    String issued();
    @JsonProperty(".expires")
    String expires();
}
