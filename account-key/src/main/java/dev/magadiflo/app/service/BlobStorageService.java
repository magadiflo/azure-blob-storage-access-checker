package dev.magadiflo.app.service;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BlobStorageService {

    @Value("${custom.blob-storage.account-key}")
    public String accountKey;

    @Value("${custom.blob-storage.account-name}")
    public String accountName;

    @Value("${custom.blob-storage.end-point}")
    public String endPoint;

    public boolean validateAccountKey() {
        try {
            String connectionString = String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;BlobEndpoint=%s;", this.accountName, this.accountKey, this.endPoint);

            // Crear cliente de servicio Blob
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();

            // Listar contenedores
            log.info("**********************************************");
            blobServiceClient.listBlobContainers().forEach(container -> log.info("Contenedor encontrado: {}", container.getName()));
            log.info("**********************************************");

            return true;
        } catch (Exception e) {
            log.error("El Account Key no es válido o no tiene permisos suficientes: {}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
