package com.techSuggestion.bot.pojo;

import com.techSuggestion.bot.dto.ProductFilter;
import com.techSuggestion.bot.models.Category;
import com.techSuggestion.bot.models.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiResponse {
    private String id;
    private String model;
    private Usage usage;
    private List<String> citations;
    private List<SearchResult> searchResults;
    private String rawContent;
    private ProductFilter productFilter;
    private Category category;
    private Double maxPrice;
    private List<Product> products;

}