package com.inviu.escogateway;

import esco.api.front.v3.models.response.BoletosItem;
import esco.api.front.v3.models.response.LoginResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class StubFactory {
    public static final DateTimeFormatter ESCO_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final long ACCOUNT = randomLong();

    public static List<BoletosItem> createTickets() {
        return LongStream.range(14000, 14003)
                .mapToObj(id -> createTicket(ACCOUNT, id, id - 100, "OPERATION_TYPE", randomLong(), null, 0, randomLong(), randomDate(), randomDate()))
                .collect(Collectors.toList());
    }

    private static String randomDate() {
        // Chosen by fair dice roll. Guaranteed to be random.
        return "2021-06-14 00:00:00";
    }

    private static long randomLong() {
        return (long) (Math.random() * 10000);
    }

    public static BoletosItem createTicket(long cuenta, long idBoleto, long numero, String tipoOperacion, double bruto, Double arancel, double derechoMercado, double neto, String fechaConcertacion, String fechaLiquidacion) {
        return BoletosItem.builder()
                .cuenta(cuenta)
                .idBoleto(idBoleto)
                .formulario("FRMBOL")
                .tipoEspecie("Sin Instrumento")
                .instrumento("Sin Instrumento")
                .numero(numero)
                .fechaConcertacion(fechaConcertacion)
                .fechaLiquidacion(fechaLiquidacion)
                .tipoOperacion(tipoOperacion)
                .cantidad(null)
                .precio(null)
                .bruto(bruto)
                .arancel(arancel)
                .derechoBolsa(0D)
                .derechoMercado(derechoMercado)
                .moneda("$")
                .neto(neto)
                .origen("BackOffice")
                .monedaArancel("$")
                .monedaDerecho("$")
                .estaAnulado(false)
                .fechaAnulacion(null)
                .build();
    }

    public static LoginResponse createLoginResponse() {
        return LoginResponse.builder()
                .accessToken("abcdef1234567890")
                .tokenType("bearer")
                .expiresIn(50)
                .refreshToken("1234567890abcdef")
                .issued(LocalDateTime.now().format(ESCO_FORMAT))
                .expires(LocalDateTime.now().plus(50, ChronoUnit.MINUTES).format(ESCO_FORMAT))
                .build();
    }
}
