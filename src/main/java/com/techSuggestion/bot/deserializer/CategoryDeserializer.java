package com.techSuggestion.bot.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.techSuggestion.bot.models.Category;

import java.io.IOException;

public class CategoryDeserializer extends JsonDeserializer<Category> {
    @Override
    public Category deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String categoryName = p.getValueAsString();
        Category category = new Category();
        category.setName(categoryName);
        return category;
    }
}
