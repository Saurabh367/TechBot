package com.techSuggestion.bot.mapper;

import com.techSuggestion.bot.dto.ChatMessage;
import com.techSuggestion.bot.dto.OpenAiChatRequest;

import java.util.Arrays;

public class OpenAiRequestMapper {

    public static OpenAiChatRequest toOpenAiChatRequest(String systemPrompt, String userPrompt) {
        ChatMessage systemMessage = new ChatMessage("system", systemPrompt);
        ChatMessage userMessage = new ChatMessage("user", userPrompt);

        // ✅ Updated model name and adjusted temperature for better creativity
        return new OpenAiChatRequest(
                "sonar-reasoning",       // safer default model
                Arrays.asList(systemMessage, userMessage),
                200,                      // max tokens
                0.7                       // temperature for more creative output
        );
    }
}
