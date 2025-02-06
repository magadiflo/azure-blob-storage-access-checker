package dev.magadiflo.app.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    public void downloadAllBlobsFromContainer(String containerName, String destinationDirectory, String sasToken) {
        try {
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .endpoint(this.blobServiceUrl + "?" + sasToken)
                    .buildClient();
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

            // Iterar sobre los blobs en el contenedor
            containerClient.listBlobs().forEach(blobItem -> {
                String blobName = blobItem.getName();
                String destinationFilePath = destinationDirectory + "\\" + blobName.replace("/", "-");

                // Crear las carpetas necesarias si no existen
                Path destinationPath = Paths.get(destinationFilePath);
                try {
                    Files.createDirectories(destinationPath.getParent());
                } catch (IOException e) {
                    log.error("No se pudo crear el directorio para {}", destinationFilePath, e);
                    return;
                }

                // Descargar cada blob
                try {
                    BlobClient blobClient = containerClient.getBlobClient(blobName);
                    blobClient.downloadToFile(destinationFilePath);
                    log.info("Archivo descargado: {}", destinationFilePath);
                } catch (Exception e) {
                    log.error("Error al descargar el blob: {}", blobName, e);
                }
            });

            log.info("Todos los archivos se han descargado correctamente del contenedor '{}'", containerName);
        } catch (Exception e) {
            log.error("Error al descargar blobs del contenedor '{}': {}", containerName, e.getMessage());
        }
    }


}
