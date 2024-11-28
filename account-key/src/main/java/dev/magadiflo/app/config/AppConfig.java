package dev.magadiflo.app.config;

import dev.magadiflo.app.service.BlobStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AppConfig {

    @Bean
    public CommandLineRunner run(BlobStorageService blobStorageService) {
        return args -> {
            boolean isValid = blobStorageService.validateAccountKey();
            log.info(isValid ? ">>> El Account Key es válido <<<" : ">>> El Account Key no es válido <<<");
        };
    }
}
