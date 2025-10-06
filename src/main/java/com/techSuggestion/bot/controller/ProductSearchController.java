package com.techSuggestion.bot.controller;

import com.techSuggestion.bot.dto.ProductDTO;
import com.techSuggestion.bot.dto.ProductFilter;
import com.techSuggestion.bot.service.OpenAiService;
import com.techSuggestion.bot.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/products")
public class ProductSearchController {

    @Autowired
    private OpenAiService openAiService;

    @Autowired
    private ProductService productService;

    @GetMapping("/search")
    public Flux<ProductDTO> searchProducts(@RequestParam String userQuery) {
        return openAiService.parseUserPrompt(userQuery)
                .flatMapMany(aiResponse -> {
                    ProductFilter filters = aiResponse.getProductFilter();
                    String category = filters != null && filters.getCategory() != null ? filters.getCategory().getName() : null;
                    Double maxPrice = (filters != null && filters.getMaxPrice() != null) ? filters.getMaxPrice() : Double.MAX_VALUE;

                    return productService.getProductsReactive(category, maxPrice);
                });
    }


}
