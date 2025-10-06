package com.techSuggestion.bot.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO{
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be 2-100 characters")
    private String name;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be zero or positive")
    private Double price;

    @Size(max = 1000, message = "Description can be up to 1000 characters")
    private String description;

    @NotBlank(message = "Buy link is required")
    @Pattern(regexp = "^(http|https)://.*$", message = "Buy link must be a valid URL")
    private String buyLink;

    @Pattern(regexp = "^(http|https)://.*$", message = "Image URL must be a valid URL")
    private String imageUrl;

    private String camera;
    private String battery;
    private String displayType;
    private String generation;
    private String design;
    private String processor;
    private String ram;
    private String storage;
    private String os;
    private String weight;
    private String dimensions;
    private String releaseDate;
    private String sourceType;
    private String fileData;

    @NotNull(message = "Category is required")
    private CategoryDTO category;


    public ProductDTO(Long id, String name, String brand, Double price, String description, String buyLink, String imageUrl, CategoryDTO categoryDTO) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.description = description;
        this.buyLink = buyLink;
        this.imageUrl = imageUrl;
        this.category = categoryDTO;
    }
}
