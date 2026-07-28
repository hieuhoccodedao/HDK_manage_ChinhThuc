package hdkmanagement.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiAPI {

    private static final String API_KEY = "YOUR_API_KEY_HERE"; // TODO: Đặt API Key ở đây hoặc file config
    // Khai báo danh sách các Model AI để dự phòng (Fallback) khi bị nghẽn mạng
    private static final String[] AI_MODELS = {
        "gemini-flash-lite-latest",
        "gemini-flash-latest",
        "gemini-2.5-flash-lite",
        "gemini-2.0-flash-lite",
        "gemini-2.0-flash",
        "gemini-1.5-pro-latest",
        "gemini-pro"
    };

    // Tái sử dụng HttpClient (Connection Pooling) để gọi API cực nhanh sau lần đầu tiên, kèm timeout 15s
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(15))
            .build();

    public static String sendChatRequest(String systemContext, String userMessage) throws Exception {

        // Kết hợp dữ liệu hệ thống vào prompt cho AI hiểu hoàn cảnh
        String combinedPrompt = systemContext + "\n\nUser asking: " + userMessage;
        
        // Thoát các ký tự đặc biệt để đưa vào chuỗi JSON an toàn
        String safePrompt = combinedPrompt.replace("\\", "\\\\")
                                          .replace("\"", "\\\"")
                                          .replace("\n", "\\n")
                                          .replace("\r", "");

        String jsonBody = "{"
                + "\"contents\": [{"
                + "\"parts\":[{\"text\": \"" + safePrompt + "\"}]"
                + "}]"
                + "}";

        // Thử lần lượt các mô hình AI từ mạnh nhất đến nhẹ nhất
        String lastError = "";
        for (String model : AI_MODELS) {
            String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + API_KEY;
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(java.time.Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, java.nio.charset.StandardCharsets.UTF_8))
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    return extractTextFromJson(response.body());
                } else {
                    lastError = "Lỗi " + response.statusCode() + " ở model " + model + ". Đang thử model khác...";
                    // Tiếp tục vòng lặp thử model khác với BẤT KỲ lỗi nào (404, 503, 403, 500...)
                    continue;
                }
            } catch (Exception e) {
                lastError = "Lỗi mạng (" + model + "): " + e.getMessage();
            }
        }
        
        throw new Exception(lastError);
    }

    /**
     * Hàm bóc tách nội dung text thủ công từ JSON phản hồi của Gemini 
     * để không cần tải thêm thư viện Gson/Jackson.
     */
    private static String extractTextFromJson(String json) {
        String searchKey = "\"text\": \"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return "Không thể đọc phản hồi từ AI.";
        
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        
        // Xử lý cẩn thận nếu text bị dính các ký tự ngoặc kép được thoát \"
        while (endIndex != -1 && json.charAt(endIndex - 1) == '\\') {
            endIndex = json.indexOf("\"", endIndex + 1);
        }

        if (endIndex == -1) return "Lỗi phân tích nội dung.";
        
        String extracted = json.substring(startIndex, endIndex);
        
        // Khôi phục ký tự
        extracted = extracted.replace("\\n", "\n")
                             .replace("\\\"", "\"")
                             .replace("\\\\", "\\")
                             .replace("\\*", "*");
                             
        return extracted;
    }
}
