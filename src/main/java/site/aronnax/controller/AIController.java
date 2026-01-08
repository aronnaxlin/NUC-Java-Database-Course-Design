package site.aronnax.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import site.aronnax.common.Result;
import site.aronnax.service.AIService;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, String> params) {
        String message = params.get("message");
        if (message == null || message.trim().isEmpty()) {
            return Result.error("请输入内容");
        }

        String response = aiService.chat(message);
        return Result.success(response);
    }
}
