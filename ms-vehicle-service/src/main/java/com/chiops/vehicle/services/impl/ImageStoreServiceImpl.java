package com.chiops.vehicle.services.impl;

import com.chiops.vehicle.libs.exceptions.exception.*;
import com.chiops.vehicle.services.ImageStoreService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.objectstorage.ObjectStorageEntry;
import io.micronaut.objectstorage.ObjectStorageOperations;
import io.micronaut.objectstorage.request.UploadRequest;
import io.micronaut.objectstorage.response.UploadResponse;
import jakarta.inject.Singleton;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ImageStoreServiceImpl implements ImageStoreService {

    private static final Logger LOG = LoggerFactory.getLogger(ImageStoreServiceImpl.class);
    private final ObjectStorageOperations<?, ?, ?> objectStorage;

    public ImageStoreServiceImpl(ObjectStorageOperations<?, ?, ?> objectStorage) {
        this.objectStorage = objectStorage;
    }

    @Override
    public String upload(String vehicleId, byte[] imageBytes, String originalFilename) {
        String contentType = getContentType(originalFilename);

        if (MediaType.APPLICATION_PDF.equals(contentType) || MediaType.APPLICATION_OCTET_STREAM.equals(contentType)) {
            LOG.error("Unsupported file type for image upload: {}", contentType);
            throw new UnsupportedMediaTypeException("Solo se permiten imágenes JPEG, PNG, GIF, BMP o WebP. No admite: " + contentType);
        }

        Optional<String> extensionOpt = getFileExtension(originalFilename);
        String extension = extensionOpt.orElseThrow(() -> {
            LOG.error("No se pudo determinar la extensión del archivo: {}", originalFilename);
            return new BadRequestException("No se pudo determinar la extensión del archivo");
        });

        String uniqueKey = vehicleId + "_" + UUID.randomUUID().toString() + extension;

        try {
            UploadRequest request = UploadRequest.fromBytes(imageBytes, uniqueKey);
            request.setContentType(contentType);
            UploadResponse<?> response = objectStorage.upload(request);
            String baseUrl = "http://localhost:8080";

            LOG.info("Imagen subida exitosamente: {}", uniqueKey);
            return baseUrl + "/vehicle/view/" + uniqueKey;

        } catch (Exception e) {
            LOG.error("Error al subir la imagen: {}", e.getMessage());
            throw new ConflictException("Error al subir la imagen: " + e.getMessage());
        }
    }

    @Override
    public HttpResponse<?> view(String filename) {
        try {
            if (filename == null || filename.isBlank()) {
                LOG.error("El campo filename es obligatorio");
                throw new BadRequestException("filename field is obligatory");
            }
            Optional<ObjectStorageEntry> entryOptional = objectStorage.retrieve(filename);

            if (entryOptional.isEmpty()) {
                LOG.error("Imagen con nombre {} no encontrada", filename);
                throw new NotFoundException("Imagen con nombre " + filename + " no encontrada");
            }

            ObjectStorageEntry entry = entryOptional.get();
            InputStream inputStream = entry.getInputStream();
            MediaType mediaType = MediaType.of(getContentType(filename));

            LOG.info("Imagen recuperada exitosamente: {}", filename);
            return HttpResponse.ok(new StreamedFile(inputStream, mediaType));
        } catch (Exception e) {
            LOG.error("Error al recuperar la imagen: {}", e.getMessage());
            throw new ConflictException("Error al recuperar la imagen: " + e.getMessage());
        }
    }

    private String getContentType(String filename) {
        if (filename == null || !filename.contains(".")) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            case "bmp":
                return MediaType.IMAGE_BMP;
            case "webp":
                return "image/webp";
            case "pdf":
                return MediaType.APPLICATION_PDF;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private Optional<String> getFileExtension(String filename) {
        if (filename == null) {
            return Optional.empty();
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return Optional.of("." + filename.substring(lastDotIndex + 1).toLowerCase());
        }
        return Optional.empty();
    }
}