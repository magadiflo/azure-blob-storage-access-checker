package dev.magadiflo.app.service;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BlobStorageService {

    @Value("${custom.blob-storage.url}")
    public String blobServiceUrl;

    public boolean validateSasToken(String sasToken) {
        try {
            // Crear el cliente de servicio usando la URL y el SAS Token
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .endpoint(this.blobServiceUrl + "?" + sasToken)
                    .buildClient();

            // Lista contenedores disponibles en blob storage para validar Token SAS
            log.info("**********************************************");
            blobServiceClient.listBlobContainers().forEach(container -> log.info("Contenedor encontrado: {}", container.getName()));
            log.info("**********************************************");

            return true;
        } catch (Exception e) {
            log.error("El token SAS no es válido o no tiene permisos suficientes: {}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
