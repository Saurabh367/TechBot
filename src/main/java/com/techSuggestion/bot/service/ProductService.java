package com.techSuggestion.bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techSuggestion.bot.dto.OpenAiChatRequest;
import com.techSuggestion.bot.dto.ProductDTO;
import com.techSuggestion.bot.dto.ProductFilter;
import com.techSuggestion.bot.mapper.Mapper;
import com.techSuggestion.bot.mapper.OpenAiRequestMapper;
import com.techSuggestion.bot.models.Product;
import com.techSuggestion.bot.models.SearchLog;
import com.techSuggestion.bot.pojo.SearchResult;
import com.techSuggestion.bot.repository.CategoryRepository;
import com.techSuggestion.bot.repository.ProductRepository;
import com.techSuggestion.bot.repository.SearchLogRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebClient webClient;
    @Autowired
    private SearchLogRepository searchLogRepository;

    @Value("${perplexity.api.key}")
    private String openAiApiKey;

    @Cacheable(value = "products", key = "#category + '-' + #maxPrice")
    public List<ProductDTO> getProducts(String category, Double maxPrice) {
        if (category == null) category = "";
        if (maxPrice == null) maxPrice = Double.MAX_VALUE;

        List<Product> products = productRepository.findByCategory_NameAndPriceLessThanEqual(category, maxPrice);
        return products.stream()
                .map(Mapper::toProductDTO)
                .collect(Collectors.toList());
    }




    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "categories", allEntries = true)
    })
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Mono<ProductDTO> createProduct(ProductDTO productDTO) {
        return Mono.fromCallable(() -> {
            Product product = Mapper.toProductEntity(productDTO);
            Product savedProduct = saveProduct(product);
            return Mapper.toProductDTO(savedProduct);
        });
    }
    public Mono<ProductFilter> parseUserPrompt(String userPrompt) {
        String systemPrompt = "Extract product filter information including category, price max," +
                " included/excluded features, brand, and any other detail from the query. Return JSON matching ProductFilter DTO.";

        OpenAiChatRequest request = OpenAiRequestMapper.toOpenAiChatRequest(systemPrompt, userPrompt);

        try {
            String requestBody = objectMapper.writeValueAsString(request);
            return webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(response -> {
                        try {
                            JsonNode root = objectMapper.readTree(response);
                            String content = root.at("/choices/0/message/content").asText();

                            // Clean and parse filter JSON substring here, similar to your earlier pattern matching
                            ProductFilter filter = objectMapper.readValue(content, ProductFilter.class);

                            // Example: extract searchResults list if present
                            List<SearchResult> searchResults = new ArrayList<>();
                            JsonNode resultsNode = root.path("search_results");
                            if (resultsNode.isArray()) {
                                for (JsonNode node : resultsNode) {
                                    SearchResult sr = objectMapper.treeToValue(node, SearchResult.class);
                                    searchResults.add(sr);
                                }
                            }

                            // Serialize filter and results as JSON for saving
                            String filterJson = objectMapper.writeValueAsString(filter);
                            String resultsJson = objectMapper.writeValueAsString(searchResults);

                            SearchLog log = new SearchLog();
                            log.setUserPrompt(userPrompt);
                            log.setProductFilterJson(filterJson);
                            log.setSearchResultsJson(resultsJson);
                            log.setTimestamp(LocalDateTime.now());
                            searchLogRepository.save(log);

                            return Mono.just(filter);

                        } catch (Exception e) {
                            e.printStackTrace();
                            return Mono.empty();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            return Mono.empty();
        }
    }


    public Flux<ProductDTO> getProductsReactive(String category, Double maxPrice) {
        if (category == null) category = "";
        if (maxPrice == null) maxPrice = Double.MAX_VALUE;

        List<Product> products = productRepository.findByCategory_NameAndPriceLessThanEqual(category, maxPrice);

        return Flux.fromIterable(products).map(Mapper::toProductDTO);
    }




}
