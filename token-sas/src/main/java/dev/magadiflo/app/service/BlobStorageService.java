package dev.magadiflo.app.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
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

    public void listBlobsInContainer(String containerName, String sasToken) {
        try {
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .endpoint(this.blobServiceUrl + "?" + sasToken)
                    .buildClient();

            // Obtener el cliente del contenedor específico
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

            // Listar los blobs (archivos) dentro del contenedor
            log.info("----------------------------------------------");
            containerClient.listBlobs().forEach(blobItem -> log.info("Archivo encontrado: {}", blobItem.getName()));
            log.info("----------------------------------------------");

        } catch (Exception e) {
            log.error("Error al obtener los blobs del contenedor {}: {}", containerName, e.getMessage());
            e.printStackTrace();
        }
    }

    public void downloadBlobFromContainer(String containerName, String blobName, String downloadFilePath, String sasToken) {
        try {
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .endpoint(this.blobServiceUrl + "?" + sasToken)
                    .buildClient();

            // Obtener el cliente de contenedor
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

            // Obtener el cliente del blob
            BlobClient blobClient = containerClient.getBlobClient(blobName);

            // Descargar el archivo al directorio local
            blobClient.downloadToFile(downloadFilePath);

            log.info("Archivo {} descargado exitosamente en {}", blobName, downloadFilePath);
        } catch (Exception e) {
            log.error("Error al descargar el archivo: {}", e.getMessage());
        }
    }

}
