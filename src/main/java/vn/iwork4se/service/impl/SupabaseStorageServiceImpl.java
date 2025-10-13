package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import vn.iwork4se.config.SupabaseConfig;
import vn.iwork4se.exception.BadRequestException;
import vn.iwork4se.service.SupabaseStorageService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SupabaseStorageServiceImpl implements SupabaseStorageService {
    private final SupabaseConfig supabaseConfig;
    private final WebClient supabaseWebClient;

    @Override
    public String uploadFile(MultipartFile file, String applicantId) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        try {
            // Generate unique file name
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("%s_%s_%s%s",
                    applicantId,
                    timestamp,
                    UUID.randomUUID().toString().substring(0, 8),
                    extension
            );

            String filePath = applicantId + "/" + fileName;

            // Upload to Supabase Storage using REST API
            String uploadUrl = String.format("/storage/v1/object/%s/%s",
                    supabaseConfig.getBucketName(),
                    filePath);

            supabaseWebClient.post()
                    .uri(uploadUrl)
                    .contentType(MediaType.valueOf(file.getContentType()))
                    .bodyValue(file.getBytes())
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("File uploaded successfully to Supabase: {}", filePath);
            return getPublicUrl(filePath);

        } catch (IOException e) {
            log.error("Error reading file: {}", e.getMessage());
            throw new BadRequestException("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error uploading file to Supabase: {}", e.getMessage());
            throw new RuntimeException("Error uploading file to Supabase: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            String deleteUrl = String.format("/storage/v1/object/%s/%s",
                    supabaseConfig.getBucketName(),
                    filePath);

            supabaseWebClient.delete()
                    .uri(deleteUrl)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("File deleted successfully from Supabase: {}", filePath);
        } catch (Exception e) {
            log.error("Error deleting file from Supabase: {}", e.getMessage());
            throw new RuntimeException("Error deleting file from Supabase: " + e.getMessage());
        }
    }

    @Override
    public String getPublicUrl(String filePath) {
        return String.format("%s/storage/v1/object/public/%s/%s",
                supabaseConfig.getSupabaseUrl(),
                supabaseConfig.getBucketName(),
                filePath
        );
    }
}
