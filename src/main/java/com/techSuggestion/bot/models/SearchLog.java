package com.techSuggestion.bot.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String userPrompt;

    @Lob
    private String productFilterJson;

    @Lob
    private String searchResultsJson;

    private LocalDateTime timestamp;

    // Getters and setters here or use Lombok @Data
}
