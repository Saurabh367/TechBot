package com.techSuggestion.bot.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usage {
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
 }