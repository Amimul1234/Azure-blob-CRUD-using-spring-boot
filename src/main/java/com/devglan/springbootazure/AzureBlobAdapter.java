package com.devglan.springbootazure;

import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class AzureBlobAdapter {

    private final CloudBlobClient cloudBlobClient;
    private final CloudBlobContainer cloudBlobContainer;

    public AzureBlobAdapter( CloudBlobClient cloudBlobClient,
                             CloudBlobContainer cloudBlobContainer ) {
        this.cloudBlobClient = cloudBlobClient;
        this.cloudBlobContainer = cloudBlobContainer;
    }

    public void createContainer( String containerName){

        CloudBlobContainer container;

        try {

            container = cloudBlobClient.getContainerReference(containerName);
            container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER,
                    new BlobRequestOptions(), new OperationContext());

        } catch (URISyntaxException | StorageException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public URI upload(MultipartFile multipartFile){
        URI uri = null;
        CloudBlockBlob blob;
        try {

            blob = cloudBlobContainer.
                    getBlockBlobReference(Objects.requireNonNull(multipartFile.getOriginalFilename()));

            blob.getProperties().setContentType(multipartFile.getContentType());

            blob.upload(multipartFile.getInputStream(), -1);
            uri = blob.getUri();
        } catch (URISyntaxException | StorageException | IOException e) {
            e.printStackTrace();
        }

        return uri;
    }

    public List<URI> listBlobs(String containerName){
        List<URI> uris = new ArrayList<>();
        try {
            CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
            for (ListBlobItem blobItem : container.listBlobs()) {
                uris.add(blobItem.getUri());
            }
        } catch (URISyntaxException | StorageException e) {
            e.printStackTrace();
        }

        return uris;
    }

    public void deleteBlob(String containerName, String blobName){
        try {
            CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
            CloudBlockBlob blobToBeDeleted = container.getBlockBlobReference(blobName);
            blobToBeDeleted.deleteIfExists();
        } catch (URISyntaxException | StorageException e) {
            e.printStackTrace();
        }
    }
}
