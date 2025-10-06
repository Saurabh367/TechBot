package com.techSuggestion.bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techSuggestion.bot.dto.ChatMessage;
import com.techSuggestion.bot.dto.OpenAiChatRequest;
import com.techSuggestion.bot.dto.ProductFilter;
import com.techSuggestion.bot.models.Category;
import com.techSuggestion.bot.models.Product;
import com.techSuggestion.bot.models.SearchLog;
import com.techSuggestion.bot.pojo.AiResponse;
import com.techSuggestion.bot.repository.CategoryRepository;
import com.techSuggestion.bot.repository.ProductRepository;
import com.techSuggestion.bot.repository.SearchLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OpenAiService {

    @Autowired
    private WebClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${perplexity.api.key:}")
    private String openAiApiKey;

    @Autowired
    private SearchLogRepository searchLogRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public OpenAiService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper, @Value("${perplexity.api.key:}") String openAiApiKey) {
        System.out.println("🧠 Using API Key: " + (openAiApiKey.isEmpty() ? "(default)" : "(configured)"));
        this.webClient = webClientBuilder.baseUrl("https://api.perplexity.ai").build();
        this.objectMapper = objectMapper;
        this.openAiApiKey = openAiApiKey.isEmpty() ? "default-key" : openAiApiKey;
    }

    public Mono<AiResponse> parseUserPrompt(String userPrompt) {
        String prompt = "Respond only with JSON containing 'productFilter' and 'products', each product having fields: name, brand, price, battery, camera, processor, ram, storage, os, display, connectivity, launchDate, description, category, etc. No explanation or text.\n";

        List<ChatMessage> messages = Arrays.asList(
                new ChatMessage("system", prompt),
                new ChatMessage("user", userPrompt)
        );

        OpenAiChatRequest request = OpenAiChatRequest.builder()
                .model("sonar-reasoning")
                .messages(messages)
                .max_tokens(2000)
                .temperature(0.7)
                .build();

        return sendRequest(request)
                .flatMap(this::processAiResponse)
                .flatMap(aiResponse -> saveSearchLogAndProducts(userPrompt, aiResponse).thenReturn(aiResponse))
                .onErrorResume(e -> {
                    e.printStackTrace();
                    AiResponse fallback = new AiResponse();
                    fallback.setRawContent("Error parsing AI response.");
                    fallback.setProductFilter(new ProductFilter());
                    fallback.setProducts(Collections.emptyList());
                    return Mono.just(fallback);
                });
    }

    private Mono<AiResponse> processAiResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            AiResponse aiResponse = new AiResponse();
            aiResponse.setId(root.path("id").asText());
            aiResponse.setModel(root.path("model").asText());

            JsonNode contentNode = root.at("/choices/0/message/content");
            if (contentNode.isMissingNode())
                return Mono.error(new IllegalArgumentException("No content field"));

            String rawContent = contentNode.asText();
            aiResponse.setRawContent(rawContent);
            String cleaned = rawContent.replaceAll("<[^>]+>", "").trim();

            Pattern pattern = Pattern.compile("\\{.*\\}", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(cleaned);
            if (matcher.find()) {
                String jsonStr = matcher.group();
                JsonNode jsonNode = objectMapper.readTree(jsonStr);

                ProductFilter filter = objectMapper.treeToValue(jsonNode.path("productFilter"), ProductFilter.class);
                aiResponse.setProductFilter(filter);

                List<Product> products = new ArrayList<>();
                JsonNode productsNode = jsonNode.path("products");
                if (productsNode.isArray()) {
                    for (JsonNode pNode : productsNode) {
                        Product product = new Product();

                        product.setName(pNode.path("name").asText(null));
                        product.setBrand(pNode.path("brand").asText(null));
                        product.setPrice(pNode.path("price").isNumber() ? pNode.get("price").doubleValue() : null);
                        product.setBattery(pNode.path("battery").asText(null));
                        product.setCamera(pNode.path("camera").asText(null));
                        product.setProcessor(pNode.path("processor").asText(null));
                        product.setRam(pNode.path("ram").asText(null));
                        product.setStorage(pNode.path("storage").asText(null));
                        product.setOs(pNode.path("os").asText(null));
                        product.setDisplayType(pNode.path("display").asText(null));
                        product.setConnectivity(pNode.path("connectivity").asText(null));
                        product.setLaunchDate(pNode.path("launchDate").asText(null));
                        product.setDescription(pNode.path("description").asText(null));
                        product.setDesign(pNode.path("design").asText(null));
                        product.setDimensions(pNode.path("dimensions").asText(null));
                        product.setFileData(pNode.path("fileData").asText(null));
                        product.setGeneration(pNode.path("generation").asText(null));
                        product.setImageUrl(pNode.path("imageUrl").asText(null));
                        product.setWeight(pNode.path("weight").asText(null));
                        product.setReleaseDate(pNode.path("releaseDate").asText(null));
                        product.setSourceType(pNode.path("sourceType").asText(null));
                        product.setBuyLink(pNode.path("buyLink").asText(null));

                        JsonNode catNode = pNode.path("category");
                        if (!catNode.isMissingNode() && catNode.has("name")) {
                            Category category = new Category();
                            category.setName(catNode.get("name").asText());
                            product.setCategory(category);
                        } else {
                            product.setCategory(null);
                        }

                        products.add(product);
                    }


                }
                aiResponse.setProducts(products);
            } else {
                aiResponse.setProductFilter(new ProductFilter());
                aiResponse.setProducts(Collections.emptyList());
            }
            return Mono.just(aiResponse);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private Mono<Void> saveSearchLogAndProducts(String userPrompt, AiResponse aiResponse) {
        try {
            Category flagshipCategory = categoryRepository.findByName("Flagship")
                    .orElseGet(() -> {
                        Category newCategory = new Category();
                        newCategory.setName("Flagship");
                        return categoryRepository.save(newCategory);
                    });
            SearchLog log = new SearchLog();
            log.setUserPrompt(userPrompt);
            log.setProductFilterJson(objectMapper.writeValueAsString(aiResponse.getProductFilter()));
            log.setSearchResultsJson(objectMapper.writeValueAsString(aiResponse.getProducts()));
            log.setTimestamp(LocalDateTime.now());
            searchLogRepository.save(log);

            if (aiResponse.getProducts() != null) {
                for (Product prod : aiResponse.getProducts()) {
                    if (prod.getPrice() == null) {
                        System.out.println("Skipping product with null price: " + prod.getName());
                        continue;
                    }

                    if (prod.getPrice() >= 60000) {
                        prod.setCategory(flagshipCategory);
                    } else if (prod.getPrice() >= 30000) {
                        Category midRangeCategory = categoryRepository.findByName("Mid-Range")
                                .orElseGet(() -> {
                                    Category newCategory = new Category();
                                    newCategory.setName("Mid-Range");
                                    return categoryRepository.save(newCategory);
                                });
                        prod.setCategory(midRangeCategory);
                    } else if (prod.getPrice() > 0) {
                        Category budgetCategory = categoryRepository.findByName("Budget")
                                .orElseGet(() -> {
                                    Category newCategory = new Category();
                                    newCategory.setName("Budget");
                                    return categoryRepository.save(newCategory);
                                });
                        prod.setCategory(budgetCategory);
                    } else {
                        // Optionally assign other category or null
                        prod.setCategory(null);
                    }


                    Optional<Product> existing = productRepository.findByNameAndBrand(prod.getName(), prod.getBrand());
                    if (existing.isPresent()) {
                        Product eProd = existing.get();
                        eProd.updateWith(prod);
                        productRepository.save(eProd);
                    } else {
                        productRepository.save(prod);
                    }
                }
            }

            return Mono.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.empty();
        }
    }

    public Mono<String> sendRequest(OpenAiChatRequest request) {
        try {
            String body = objectMapper.writeValueAsString(request);
            return webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(b -> System.out.println("Raw AI response: " + b));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Mono.error(ex);
        }
    }

    public Mono<String> sendRawUserPrompt(String prompt) {
        List<ChatMessage> messages = Arrays.asList(
                new ChatMessage("system", "You answer the user promptly"),
                new ChatMessage("user", prompt)
        );

        OpenAiChatRequest request = OpenAiChatRequest.builder()
                .model("sonar-reasoning")
                .messages(messages)
                .max_tokens(500)
                .temperature(0.7)
                .build();

        return sendRequest(request);
    }


    public Mono<String> generateDescription(String productPrompt) {
        List<ChatMessage> messages = Arrays.asList(
                new ChatMessage("system", "Generate catchy product description"),
                new ChatMessage("user", productPrompt)
        );

        OpenAiChatRequest request = OpenAiChatRequest.builder()
                .model("sonar-reasoning")
                .messages(messages)
                .max_tokens(500)
                .temperature(0.7)
                .build();

        return sendRequest(request);
    }

}
