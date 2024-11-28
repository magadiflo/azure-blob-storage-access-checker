package dev.magadiflo.app.config;

import dev.magadiflo.app.service.BlobStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AppConfig {

    @Value("${custom.blob-storage.sas-token}")
    public String sasToken;

    @Bean
    public CommandLineRunner run(BlobStorageService blobStorageService) {
        return args -> {
            boolean isValid = blobStorageService.validateSasToken(this.sasToken);
            log.info(isValid ? ">>> El Token SAS es válido <<<" : ">>> El Token SAS no es válido <<<");
        };
    }
}
