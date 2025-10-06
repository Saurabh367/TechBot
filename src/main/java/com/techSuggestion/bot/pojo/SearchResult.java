package com.techSuggestion.bot.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult {
    private String title;
    private String url;
    private String snippet;
    private String source;
    private String date;
    private String lastUpdated;
 }