package esco.api.front.v3.models.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.Optional;

@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = CtaCorrienteConsolidadoItem.class)
public interface AbstractCtaCorrienteConsolidadoItem extends Jsonable {
    Long codComitente();
    Long codCtaCorriente();
    @Nullable Long codCtaCorrienteIt();
    @Nullable String fechaConcertacion();
    @Nullable String fechaLiquidacion();
    @Nullable String fechaOrden();
    @Nullable String detalle();
    Boolean esDisponible();
    @Nullable String instrumento();
    @Nullable String codigoInstrumento();
    @Nullable String monedaSimb();
    @Nullable String monedaISO();
    Optional<Double> cantidadVN();
    Optional<Double> precio();
    @Nullable Double importeBruto();
    @Nullable Double importeNeto();
    @Nullable Double saldo();
    @Nullable Double saldoMonPais();
    Boolean esSaldoAnterior();
    @Nullable String tipoItem();
    @Nullable String codFormulario();
    @Nullable Long claveFormulario();
    @Nullable Double codCtaCorrienteValor();
    @Nullable Double derecho();
    Optional<Double> porcArancel();
    Optional<Double> arancel();
    Optional<Double> impIVAArancel();
    Optional<Double> porcDerechoBls();
    Optional<Double> porcDerechoMerc();
    Optional<Double> impIVADerechoMerc();
    @Nullable Long codigoEspecie();
    Optional<Boolean> esSaldo();
    @Nullable Long codMonedaCoti();
    @Nullable String origen();
    @Nullable Long timeStamp();
    @Nullable String numCuentaDepositario();
    @Nullable String depositario();
    @Nullable Long codDepositario();

}
