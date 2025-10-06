package com.techSuggestion.bot.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.techSuggestion.bot.deserializer.CategoryDeserializer;
import com.techSuggestion.bot.deserializer.PriceDeserializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Critical for AUTO_INCREMENT
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 255)
    private String brand;

    @Column(nullable = false)
    @JsonDeserialize(using = PriceDeserializer.class)
    private Double price;

    @Column(columnDefinition = "TEXT", length = 5000)
    private String description;

    @Column(length = 1000)
    private String buyLink;

    @Column(length = 1000)
    private String imageUrl;

    @Column(length = 500)
    private String camera;

    @Column(length = 500)
    private String battery;

    @Column(length = 500)
    private String displayType;

    @Column(length = 500)
    private String generation;

    @Column(length = 500)
    private String design;

    @Column(length = 500)
    private String processor;

    @Column(length = 500)
    private String ram;

    @Column(length = 500)
    private String storage;

    @Column(length = 500)
    private String os;

    @Column(length = 255)
    private String weight;

    @Column(length = 255)
    private String dimensions;

    @Column(length = 255)
    private String releaseDate;

    @Column(length = 100)
    private String sourceType;

    @Column(length = 1000)
    private String fileData;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JsonDeserialize(using = CategoryDeserializer.class)
    private Category category;

    @Column(length = 100)
    private String connectivity;
    @Column(length = 100)
    private String launchDate;

    public void updateWith(Product other) {
        if (other == null) return;

        if (other.getName() != null) this.setName(other.getName());
        if (other.getBrand() != null) this.setBrand(other.getBrand());
        if (other.getPrice() != null) this.setPrice(other.getPrice());
        if (other.getDescription() != null) this.setDescription(other.getDescription());
        if (other.getBuyLink() != null) this.setBuyLink(other.getBuyLink());
        if (other.getImageUrl() != null) this.setImageUrl(other.getImageUrl());
        if (other.getCamera() != null) this.setCamera(other.getCamera());
        if (other.getBattery() != null) this.setBattery(other.getBattery());
        if (other.getDisplayType() != null) this.setDisplayType(other.getDisplayType());
        if (other.getGeneration() != null) this.setGeneration(other.getGeneration());
        if (other.getDesign() != null) this.setDesign(other.getDesign());
        if (other.getProcessor() != null) this.setProcessor(other.getProcessor());
        if (other.getRam() != null) this.setRam(other.getRam());
        if (other.getStorage() != null) this.setStorage(other.getStorage());
        if (other.getOs() != null) this.setOs(other.getOs());
        if (other.getWeight() != null) this.setWeight(other.getWeight());
        if (other.getDimensions() != null) this.setDimensions(other.getDimensions());
        if (other.getReleaseDate() != null) this.setReleaseDate(other.getReleaseDate());
        if (other.getSourceType() != null) this.setSourceType(other.getSourceType());
        if (other.getFileData() != null) this.setFileData(other.getFileData());

        if (other.getCategory() != null) {
            if (this.getCategory() == null) {
                this.setCategory(other.getCategory());
            } else {
                // Optionally update category name or other fields if needed
            }
        }
    }

}
