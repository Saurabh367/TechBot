package com.techSuggestion.bot.controller;

import com.techSuggestion.bot.dto.ProductDTO;
import com.techSuggestion.bot.mapper.Mapper;
import com.techSuggestion.bot.models.Product;
import com.techSuggestion.bot.pojo.AiResponse;
import com.techSuggestion.bot.repository.ProductRepository;
import com.techSuggestion.bot.service.OpenAiService;
import com.techSuggestion.bot.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    public OpenAiService openAiService;
    @Autowired
    public ProductService productService;
    @Autowired
    public ProductRepository productRepository;

    @Autowired
    public ChatbotController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping("/parse")
    public Mono<AiResponse> parseUserPrompt(@RequestBody String userQuery) {
        return openAiService.parseUserPrompt(userQuery);
    }

    @GetMapping("/products")
    public Flux<ProductDTO> getProductsReactive(String category, Double maxPrice) {
        if (category == null) category = "";
        if (maxPrice == null) maxPrice = Double.MAX_VALUE;

        List<Product> products = productRepository.findByCategory_NameAndPriceLessThanEqual(category, maxPrice);
        return Flux.fromIterable(products).map(Mapper::toProductDTO);
    }


}
