package com.project.stationery_be_server.holder;

import com.project.stationery_be_server.repository.ImageRepository;
import org.springframework.stereotype.Component;

@Component
public class ImageRepositoryHolder {
    public static ImageRepository imageRepository;

    public ImageRepositoryHolder(ImageRepository repository) {
        ImageRepositoryHolder.imageRepository = repository;

    }
}
