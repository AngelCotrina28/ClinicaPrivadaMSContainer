package com.clinica.citas.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import java.time.ZoneId;
import java.util.List;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoTemporalMigration implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(MongoTemporalMigration.class);

    private final MongoTemplate mongoTemplate;
    private final String legacyTimezone;

    public MongoTemporalMigration(
            MongoTemplate mongoTemplate,
            @Value("${app.mongo.legacy-timezone:UTC}") String legacyTimezone) {
        this.mongoTemplate = mongoTemplate;
        this.legacyTimezone = ZoneId.of(legacyTimezone).getId();
    }

    @Override
    public void run(ApplicationArguments args) {
        MongoCollection<Document> citas = mongoTemplate.getCollection("citas");
        Document legacyTemporalTypes = new Document("$or", List.of(
                new Document("fecha", new Document("$type", "date")),
                new Document("horaInicio", new Document("$type", "date")),
                new Document("horaFin", new Document("$type", "date"))));

        Document normalizedValues = new Document()
                .append("fecha", formatDateWhenRequired("$fecha", "%Y-%m-%d", legacyTimezone))
                .append("horaInicio", formatDateWhenRequired("$horaInicio", "%H:%M:%S", legacyTimezone))
                .append("horaFin", formatDateWhenRequired("$horaFin", "%H:%M:%S", legacyTimezone));

        UpdateResult result = citas.updateMany(
                legacyTemporalTypes,
                List.of(new Document("$set", normalizedValues)));

        if (result.getModifiedCount() > 0) {
            logger.info("Se normalizaron {} citas con el formato temporal anterior.", result.getModifiedCount());
        }
    }

    private Document formatDateWhenRequired(String fieldReference, String format, String timezone) {
        return new Document("$cond", List.of(
                new Document("$eq", List.of(new Document("$type", fieldReference), "date")),
                new Document("$dateToString", new Document("date", fieldReference)
                        .append("format", format)
                        .append("timezone", timezone)),
                fieldReference));
    }
}
