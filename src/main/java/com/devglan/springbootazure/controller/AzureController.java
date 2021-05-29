package com.devglan.springbootazure.controller;

import com.devglan.springbootazure.AzureBlobAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user/image")
public class AzureController {

    @Autowired
    private AzureBlobAdapter azureBlobAdapter;

    @PostMapping("/container")
    public void createContainer(@RequestBody String containerName){
        azureBlobAdapter.createContainer(containerName);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam MultipartFile multipartFile){
        URI url = azureBlobAdapter.upload(multipartFile);
        return new ResponseEntity<>(url.toString(), HttpStatus.OK);
    }

    @GetMapping("/getAllBlobs")
    public ResponseEntity<List<URI>> getAllBlobs(@RequestParam String containerName){
        List<URI> uris = azureBlobAdapter.listBlobs(containerName);
        return ResponseEntity.ok(uris);
    }

    @DeleteMapping("/deleteFromBlob")
    public void delete(@RequestParam String containerName, @RequestParam String blobName){
        azureBlobAdapter.deleteBlob(containerName, blobName);
    }


}
