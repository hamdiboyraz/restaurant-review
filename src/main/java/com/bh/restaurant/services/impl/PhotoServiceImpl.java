package com.bh.restaurant.services.impl;

import com.bh.restaurant.domain.entities.Photo;
import com.bh.restaurant.services.PhotoService;
import com.bh.restaurant.services.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final StorageService storageService;

    @Override
    public Photo uploadPhoto(MultipartFile file) {
        // Generate a unique ID for the photo
        String photoId = UUID.randomUUID().toString();
        // Store the file and get its URL
        String url = storageService.store(file, photoId);

        // Create and populate the photo entity
        return Photo.builder()
                .url(url)
                .uploadDate(LocalDateTime.now())
                .build();
    }

    @Override
    public Optional<Resource> getPhotoAsResource(String id) {
        return storageService.loadAsResource(id);
    }
}
