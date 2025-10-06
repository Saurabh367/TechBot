package com.techSuggestion.bot.dto;

import com.techSuggestion.bot.models.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilter {
    private Double maxPrice;
    private List<String> brands;
    private List<String> includeFeatures;
    private List<String> excludeFeatures;
    private Category category;
    private String rawResponse;
    // Additional optional filters
    private String camera;           // e.g., "good", "48MP"
    private String battery;          // e.g., "4000mAh", "long-lasting"
    private Double screenSize;       // e.g., 6.5 (inches)
    private String displayType;      // e.g., "AMOLED", "LCD"
    private String generation;       // e.g., "5G", "4G"
    private String design;           // e.g., "sleek", "rugged"
    private String releaseDate;
    private String processor;
    private String ram;
    private String storage;
    private String os;
    private String weight;
    private String dimensions;
}
