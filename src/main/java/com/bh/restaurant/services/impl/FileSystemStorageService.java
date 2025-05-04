package com.bh.restaurant.services.impl;

import com.bh.restaurant.exceptions.StorageException;
import com.bh.restaurant.services.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
@Slf4j
public class FileSystemStorageService implements StorageService {

    @Value("${app.storage.location:uploads}") // Default value if not set in properties
    private String storageLocation; // Directory for file storage
    private Path rootLocation; // Path to the root location for file storage

    @PostConstruct // This method is called after the bean's properties have been set
    public void init() {
        rootLocation = Paths.get(storageLocation);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e);
        }
    }

    @Override
    public String store(MultipartFile file, String filename) {
        // Check for empty files
        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file");
        }

        // Create the final filename with extension
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String finalFilename = filename + "." + extension;

        // Resolve and normalize the destination path
        Path destinationFile = this.rootLocation
                .resolve(Paths.get(finalFilename)) // Resolve the filename to the root location
                .normalize() // Normalize the path to remove any redundant elements
                .toAbsolutePath(); // Convert to an absolute path

        // Security check to prevent directory traversal
        if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
            throw new StorageException("Cannot store file outside current directory");
        }

        // Copy the file to the destination
        // Use try-with-resources to ensure the InputStream is closed after use
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            return finalFilename;
        } catch (IOException e) {
            throw new StorageException("Failed to store file", e);
        }
    }

    @Override
    public Optional<Resource> loadAsResource(String filename) {
        try {
            // Resolve the file path relative to our root location
            Path file = rootLocation.resolve(filename);

            // Create a Resource object from the file path
            Resource resource = new UrlResource(file.toUri());

            // Check if the resource exists and is readable
            if (resource.exists() || resource.isReadable()) {
                return Optional.of(resource);
            } else {
                return Optional.empty();
            }
        } catch (MalformedURLException e) {
            log.warn("Could not read file: " + filename, e);
            return Optional.empty();        }
    }
}
