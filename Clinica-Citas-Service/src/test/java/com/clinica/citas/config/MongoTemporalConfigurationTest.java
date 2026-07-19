package com.clinica.citas.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class MongoTemporalConfigurationTest {

    @Test
    void convierteFechasSinDependerDeZonaHoraria() {
        LocalDate date = LocalDate.of(2026, 8, 5);

        String stored = MongoTemporalConfiguration.LocalDateToStringConverter.INSTANCE.convert(date);

        assertThat(stored).isEqualTo("2026-08-05");
        assertThat(MongoTemporalConfiguration.StringToLocalDateConverter.INSTANCE.convert(stored)).isEqualTo(date);
    }

    @Test
    void convierteHorasSembradasYEscribeUnFormatoOrdenable() {
        LocalTime time = LocalTime.of(9, 30);

        String stored = MongoTemporalConfiguration.LocalTimeToStringConverter.INSTANCE.convert(time);

        assertThat(stored).isEqualTo("09:30:00");
        assertThat(MongoTemporalConfiguration.StringToLocalTimeConverter.INSTANCE.convert(stored)).isEqualTo(time);
        assertThat(MongoTemporalConfiguration.StringToLocalTimeConverter.INSTANCE.convert("09:30")).isEqualTo(time);
    }
}
