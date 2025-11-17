package vn.iwork4se.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vn.iwork4se.service.GeminiService;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeminiServiceImpl implements GeminiService {
    
    @Value("${gemini.api.key:}")
    private String apiKey;
    
    @Value("${gemini.api.model:gemini-pro}")
    private String model;
    
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={key}";
    private final RestTemplate restTemplate;
    private final Gson gson = new Gson();
    private final vn.iwork4se.service.AIDataRetrievalService dataRetrievalService;
    
    // System prompt để định hướng AI là trợ lý tìm việc
    private static final String SYSTEM_PROMPT = "Bạn là một trợ lý AI thông minh và thân thiện của nền tảng tìm việc làm iWork4SE. " +
            "Nhiệm vụ của bạn là hỗ trợ các ứng viên (applicant) tìm việc làm phù hợp. " +
            "Bạn có thể giúp họ:\n" +
            "- Tư vấn về cách viết CV, cover letter\n" +
            "- Hướng dẫn chuẩn bị phỏng vấn\n" +
            "- Tìm kiếm việc làm phù hợp với kỹ năng\n" +
            "- Tư vấn phát triển sự nghiệp\n" +
            "- Trả lời các câu hỏi về quy trình ứng tuyển\n\n" +
            "Hãy trả lời một cách thân thiện, chuyên nghiệp và hữu ích. " +
            "Nếu không chắc chắn, hãy thừa nhận và đề xuất họ liên hệ với bộ phận hỗ trợ.\n\n" +
            "Hãy trả lời bằng tiếng Việt.";

    @Override
    public String generateResponse(String userMessage, String conversationHistory) {
        try {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.error("[GEMINI] API key is not configured. Please set GEMINI_API_KEY environment variable.");
                return "Xin lỗi, dịch vụ AI chưa được cấu hình. Vui lòng liên hệ quản trị viên.";
            }
            
            log.debug("[GEMINI] Generating response for message: {}", userMessage);
            
            // Tạo request body
            JsonObject requestBody = new JsonObject();
            JsonArray contents = new JsonArray();
            JsonObject content = new JsonObject();
            JsonArray parts = new JsonArray();
            
            // Retrieve relevant data from database
            String relevantData = dataRetrievalService.retrieveRelevantData(userMessage);
            
            // Thêm system prompt và lịch sử hội thoại nếu có
            String fullPrompt = SYSTEM_PROMPT;
            
            // Add relevant data if available
            if (relevantData != null && !relevantData.trim().isEmpty()) {
                fullPrompt += "\n\nQUAN TRỌNG: Dưới đây là dữ liệu thực tế từ hệ thống. " +
                             "Bạn PHẢI sử dụng thông tin này để trả lời câu hỏi của người dùng. " +
                             "Nếu người dùng hỏi về việc làm hoặc công ty, hãy tham khảo dữ liệu này:\n" +
                             relevantData;
            }
            
            if (conversationHistory != null && !conversationHistory.trim().isEmpty()) {
                fullPrompt += "\n\nLịch sử hội thoại:\n" + conversationHistory;
            }
            fullPrompt += "\n\nNgười dùng: " + userMessage;
            
            JsonObject textPart = new JsonObject();
            textPart.addProperty("text", fullPrompt);
            parts.add(textPart);
            
            content.add("parts", parts);
            contents.add(content);
            requestBody.add("contents", contents);
            
            // Cấu hình generation
            JsonObject generationConfig = new JsonObject();
            generationConfig.addProperty("temperature", 0.7);
            generationConfig.addProperty("topK", 40);
            generationConfig.addProperty("topP", 0.95);
            generationConfig.addProperty("maxOutputTokens", 2048);
            requestBody.add("generationConfig", generationConfig);
            
            // Gọi API
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            
            HttpEntity<String> entity = new HttpEntity<>(gson.toJson(requestBody), headers);
            
            String url = GEMINI_API_URL.replace("{model}", model).replace("{key}", apiKey);
            
            log.debug("[GEMINI] Calling Gemini API: {}", url);
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonObject responseJson = gson.fromJson(response.getBody(), JsonObject.class);
                
                if (responseJson.has("candidates") && responseJson.getAsJsonArray("candidates").size() > 0) {
                    JsonObject candidate = responseJson.getAsJsonArray("candidates").get(0).getAsJsonObject();
                    if (candidate.has("content")) {
                        JsonObject contentObj = candidate.getAsJsonObject("content");
                        if (contentObj.has("parts") && contentObj.getAsJsonArray("parts").size() > 0) {
                            String aiResponse = contentObj.getAsJsonArray("parts")
                                    .get(0).getAsJsonObject()
                                    .get("text").getAsString();
                            log.info("[GEMINI] Successfully generated response");
                            return aiResponse;
                        }
                    }
                }
                
                log.warn("[GEMINI] Unexpected response format: {}", response.getBody());
                return "Xin lỗi, tôi gặp vấn đề khi xử lý câu trả lời. Vui lòng thử lại sau.";
            } else {
                log.error("[GEMINI] API call failed with status: {}", response.getStatusCode());
                return "Xin lỗi, tôi không thể kết nối đến dịch vụ AI. Vui lòng thử lại sau.";
            }
            
        } catch (Exception e) {
            log.error("[GEMINI] Error generating response", e);
            return "Xin lỗi, đã xảy ra lỗi khi xử lý yêu cầu của bạn. Vui lòng thử lại sau.";
        }
    }
}

