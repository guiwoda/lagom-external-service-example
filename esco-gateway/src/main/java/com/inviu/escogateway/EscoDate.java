package com.inviu.escogateway;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public interface EscoDate {
    static OffsetDateTime parse(String rawDate) {
        String iso = rawDate.trim().replace(" ", "T");

        return LocalDateTime.parse(iso).atOffset(ZoneOffset.UTC);
    }
}
