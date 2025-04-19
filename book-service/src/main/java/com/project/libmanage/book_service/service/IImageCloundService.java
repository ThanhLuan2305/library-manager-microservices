package com.project.libmanage.book_service.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IImageCloundService {

    String uploadImage(MultipartFile imgUrl) throws IOException;

    boolean deleteImage(String fileName);

    String updateImage(String oldFileName, MultipartFile newFile);

    String getPreviewUrl(String fileName);

    void validateFile(MultipartFile file);

}
