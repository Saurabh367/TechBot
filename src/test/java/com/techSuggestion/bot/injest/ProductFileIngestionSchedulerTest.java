package com.techSuggestion.bot.injest;

import com.techSuggestion.bot.dto.ProductDTO;
import com.techSuggestion.bot.ingest.ProductFileIngestionScheduler;
import com.techSuggestion.bot.service.ProductService;
import com.techSuggestion.bot.service.OpenAiService;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductFileIngestionSchedulerTest {

    @Mock
    private ProductService productService;

    @Mock
    private OpenAiService openAiService;

    @InjectMocks
    private ProductFileIngestionScheduler scheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Ingest CSV files successfully")
    void ingestCsvFilesSuccessfully() throws IOException {
        Path mockFile = mock(Path.class);
        when(mockFile.toString()).thenReturn("test.csv");

        scheduler.processFile(mockFile);

        verify(productService, atLeastOnce()).createProduct(any(ProductDTO.class));
    }

    @Test
    @DisplayName("Skip invalid product during file processing")
    void skipInvalidProductDuringFileProcessing() throws IOException {
        CSVRecord mockRecord = mock(CSVRecord.class);
        when(mockRecord.get("name")).thenReturn("");
        when(mockRecord.get("brand")).thenReturn("Brand");

        ProductDTO dto = scheduler.mapRecordToProduct(mockRecord);
        assertFalse(scheduler.isValidProduct(dto));
    }

    @Test
    @DisplayName("Generate product description when missing")
    void generateProductDescriptionWhenMissing() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Product");
        dto.setBrand("Brand");
        dto.setDescription(null);

        when(openAiService.generateDescription(anyString())).thenReturn(Mono.just("Generated Description"));
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(Mono.empty());

        scheduler.processProduct(dto).block();

        assertNotNull(dto.getDescription());
        verify(productService).createProduct(dto);
    }

    @Test
    @DisplayName("Handle invalid price gracefully")
    void handleInvalidPriceGracefully() {
        Double price = scheduler.parsePrice("invalid");
        assertNull(price);
    }

    @Test
    @DisplayName("Create directories if not exist")
    void createDirectoriesIfNotExist() throws IOException {
        Path mockPath = mock(Path.class);
        when(mockPath.toString()).thenReturn("mockDir");

        Path result = scheduler.createDirectory("mockDir");
        assertNotNull(result);
    }
}
