package com.clinica.citas.config;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration(proxyBeanMethods = false)
public class MongoTemporalConfiguration {

    private static final DateTimeFormatter STORAGE_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Bean
    MongoCustomConversions mongoCustomConversions() {
        return MongoCustomConversions.create(adapter -> adapter
                .registerConverter(StringToLocalDateConverter.INSTANCE)
                .registerConverter(LocalDateToStringConverter.INSTANCE)
                .registerConverter(StringToLocalTimeConverter.INSTANCE)
                .registerConverter(LocalTimeToStringConverter.INSTANCE));
    }

    @ReadingConverter
    enum StringToLocalDateConverter implements Converter<String, LocalDate> {
        INSTANCE;

        @Override
        public LocalDate convert(String source) {
            return LocalDate.parse(source, DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    @WritingConverter
    enum LocalDateToStringConverter implements Converter<LocalDate, String> {
        INSTANCE;

        @Override
        public String convert(LocalDate source) {
            return DateTimeFormatter.ISO_LOCAL_DATE.format(source);
        }
    }

    @ReadingConverter
    enum StringToLocalTimeConverter implements Converter<String, LocalTime> {
        INSTANCE;

        @Override
        public LocalTime convert(String source) {
            return LocalTime.parse(source, DateTimeFormatter.ISO_LOCAL_TIME);
        }
    }

    @WritingConverter
    enum LocalTimeToStringConverter implements Converter<LocalTime, String> {
        INSTANCE;

        @Override
        public String convert(LocalTime source) {
            return STORAGE_TIME_FORMAT.format(source);
        }
    }
}
