package com.chiops.vehicle.services;


import io.micronaut.http.HttpResponse;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Singleton;


public interface ImageStoreService {
    
    String upload(String vehicleId, byte[] imageBytes, String originalFilename);

    HttpResponse<?> view(String filename);
}
