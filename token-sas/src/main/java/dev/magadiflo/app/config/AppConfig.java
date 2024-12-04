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

    @Value("${custom.blob-storage.container-name}")
    public String containerName;

    @Bean
    public CommandLineRunner run(BlobStorageService blobStorageService) {
        return args -> {
            boolean isValid = blobStorageService.validateSasToken(this.sasToken);
            log.info(isValid ? ">>> El Token SAS es válido <<<" : ">>> El Token SAS no es válido <<<");
            String userHome = System.getProperty("user.home");

            if (isValid) {
                log.info("Listando contenido del contenedor: {}", this.containerName);
                blobStorageService.listBlobsInContainer(this.containerName, this.sasToken);

//                log.info("Descargando un archivo del contenedor: {}", this.containerName);
//                String downloadFilePath = userHome + "\\Downloads\\reclamosnew_v2_respuestaIA.csv";
//                String blobName = "resultados/reclamosnew_v2_respuestaIA.csv";
//                blobStorageService.downloadBlobFromContainer(this.containerName, blobName, downloadFilePath, this.sasToken);

                log.info("Descargando todos los archivos del contenedor: {}", this.containerName);
                String downloadDir = userHome + "\\Downloads\\" + this.containerName;
                blobStorageService.downloadAllBlobsFromContainer(this.containerName, downloadDir, this.sasToken);
            }

        };
    }
}
