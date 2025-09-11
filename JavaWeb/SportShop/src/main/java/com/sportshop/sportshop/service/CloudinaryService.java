package com.sportshop.sportshop.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface CloudinaryService {
    // Giữ method cũ (đang dùng nơi khác)
    String uploadFile(MultipartFile file) throws IOException;

    // Overload: chỉ định folder (vd: "comments")
    String uploadFile(MultipartFile file, String folder) throws IOException;

    // Tiện: upload nhiều file và trả về list URL
    List<String> uploadFiles(List<MultipartFile> files, String folder) throws IOException;
}
