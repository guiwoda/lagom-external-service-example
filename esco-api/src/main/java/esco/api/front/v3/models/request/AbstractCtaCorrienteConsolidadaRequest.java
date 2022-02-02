package esco.api.front.v3.models.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;

import java.time.OffsetDateTime;
import java.util.Optional;

@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = CtaCorrienteConsolidadaRequest.class)
public interface AbstractCtaCorrienteConsolidadaRequest extends Jsonable {
    Long cuenta();
    OffsetDateTime fechaDesde();
    OffsetDateTime fechaHasta();
    Optional<Boolean> porConcertacion();
}
