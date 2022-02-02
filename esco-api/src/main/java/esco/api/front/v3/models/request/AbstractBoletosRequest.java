package esco.api.front.v3.models.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = BoletosRequest.class)
public interface AbstractBoletosRequest extends Jsonable {
    @Nullable String cuentas();
    LocalDateTime fechaDesde();
    LocalDateTime fechaHasta();
    Boolean porConcertacion();
    Boolean esConsolidado();
    Boolean incluyeAnulados();
}
