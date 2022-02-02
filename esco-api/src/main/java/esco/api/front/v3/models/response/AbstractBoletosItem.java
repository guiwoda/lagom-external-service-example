package esco.api.front.v3.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = BoletosItem.class)
public interface AbstractBoletosItem extends Jsonable {
    Long cuenta();
    Long idBoleto();
    String formulario();
    String tipoEspecie();
    String instrumento();
    Long numero();
    @Nullable
    @JsonProperty("FechaConcertacion")
    String fechaConcertacion();
    @Nullable
    @JsonProperty("FechaLiquidacion")
    String fechaLiquidacion();
    String tipoOperacion();
    @Nullable Double cantidad();
    @Nullable Double precio();
    Double bruto();
    @Nullable Double arancel();
    Double derechoBolsa();
    Double derechoMercado();
    String moneda();
    Double neto();
    String origen();
    String monedaArancel();
    String monedaDerecho();
    Boolean estaAnulado();
    @Nullable String fechaAnulacion();
    List<AbstractBoletoEjecucionItem> ejecuciones();

    default Optional<Double> extractInvested(List<CtaCorrienteConsolidadoItem> movements) {
        return findRelevantMovements(movements).stream()
                .filter(movement -> !movement.esDisponible())
                .map(movement -> Math.abs(movement.importeBruto()))
                .findFirst();
    }

    default List<CtaCorrienteConsolidadoItem> findRelevantMovements(List<CtaCorrienteConsolidadoItem> movements){
        return movements.stream()
                .filter(movement -> Objects.equals(idBoleto(), movement.claveFormulario()))
                .collect(Collectors.toList());
    }
}
