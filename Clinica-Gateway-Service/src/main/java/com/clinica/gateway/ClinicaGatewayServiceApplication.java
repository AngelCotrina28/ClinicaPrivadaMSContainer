package com.clinica.gateway;

import io.github.cdimascio.dotenv.Dotenv;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClinicaGatewayServiceApplication {

    public static void main(String[] args) {
        Path currentDir = Path.of(System.getProperty("user.dir"));
        Path dotenvDir = Files.exists(currentDir.resolve(".env"))
                ? currentDir
                : currentDir.resolve("Clinica-Gateway-Service");

        Dotenv dotenv = Dotenv.configure()
                .directory(dotenvDir.toString())
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry -> {
            if (System.getProperty(entry.getKey()) == null && System.getenv(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        });

        SpringApplication.run(ClinicaGatewayServiceApplication.class, args);
    }
}
