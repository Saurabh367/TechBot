package com.techSuggestion.bot.dto;

 import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenAiChatRequest {
    private String model;
    private List<ChatMessage> messages;
    private int max_tokens;
    private double temperature;
}
