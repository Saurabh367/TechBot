package com.techSuggestion.bot.mapper;

import com.techSuggestion.bot.dto.CategoryDTO;
import com.techSuggestion.bot.dto.ProductDTO;
import com.techSuggestion.bot.models.Category;
import com.techSuggestion.bot.models.Product;

public class Mapper {
    public static CategoryDTO toCategoryDTO(Category category) {
        if (category == null) return null;
        return new CategoryDTO(category.getId(), category.getName());
    }

    public static Category toCategoryEntity(CategoryDTO dto) {
        if (dto == null) return null;
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        return category;
    }

    public static ProductDTO toProductDTO(Product product) {
        if (product == null) return null;
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getPrice(),
                product.getDescription(),
                product.getBuyLink(),
                product.getImageUrl(),
                toCategoryDTO(product.getCategory())
        );
    }

    public static Product toProductEntity(ProductDTO dto) {
        if (dto == null) return null;
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setBrand(dto.getBrand());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());
        product.setBuyLink(dto.getBuyLink());
        product.setImageUrl(dto.getImageUrl());
        product.setCategory(toCategoryEntity(dto.getCategory()));
        return product;
    }
}
