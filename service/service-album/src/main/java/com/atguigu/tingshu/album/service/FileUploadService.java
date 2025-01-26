package com.atguigu.tingshu.album.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author admin
 * @version 1.0
 */
public interface FileUploadService {
    String fileUpload(MultipartFile file);
}
