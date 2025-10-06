package com.techSuggestion.bot.repository;

import com.techSuggestion.bot.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_NameAndPriceLessThanEqual(String categoryName, Double maxPrice);
    List<Product> findByNameContainingIgnoreCase(String search);
    List<Product> findByCategory_Name(String categoryName);
    List<Product> findByPriceLessThanEqual(Double maxPrice);
    List<Product> findByBrandIgnoreCase(String brand);
    List<Product> findByCameraContainingIgnoreCase(String camera);
    List<Product> findByBatteryContainingIgnoreCase(String battery);
    List<Product> findByDisplayTypeIgnoreCase(String displayType);
    List<Product> findByGenerationIgnoreCase(String generation);
    Optional<Product> findByNameAndBrand(String name, String brand);
    List<Product> findByProcessorContainingIgnoreCase(String processor);
    List<Product> findByRamContainingIgnoreCase(String ram);
    List<Product> findByStorageContainingIgnoreCase(String storage);
    List<Product> findByDesignContainingIgnoreCase(String design);
    List<Product> findByIdIn(List<Long> ids);
}



