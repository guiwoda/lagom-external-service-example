package esco.api.front.v3.models.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = BoletoEjecucionItem.class)
public interface AbstractBoletoEjecucionItem extends Jsonable {
    Long codBoleto();
    Long numMinuta();
    Long numEjecucion();
    Long codOrden();
    Long numOrden();
    Double cantidad();
    Double precio();
}
