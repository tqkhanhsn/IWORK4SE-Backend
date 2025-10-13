package vn.iwork4se.service;

import org.springframework.web.multipart.MultipartFile;

public interface SupabaseStorageService {
    String uploadFile(MultipartFile file, String applicantId);
    void deleteFile(String filePath);
    String getPublicUrl(String filePath);
}
