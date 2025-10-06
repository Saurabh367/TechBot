package com.techSuggestion.bot.ingest;

import com.techSuggestion.bot.dto.ProductDTO;
import com.techSuggestion.bot.service.ProductService;
import com.techSuggestion.bot.service.OpenAiService;
import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ProductFileIngestionScheduler {

    @Value("${app.ingestion.upload-dir}")
    private String uploadDirPath;

    @Value("${app.ingestion.archive-dir}")
    private String archiveDirPath;

    private Path uploadDir;
    private Path archiveDir;

    @Autowired
    private ProductService productService;

    @Autowired
    private OpenAiService openAiService;

    @PostConstruct
    public void init() throws IOException {
        uploadDir = createDirectory(uploadDirPath);
        archiveDir = createDirectory(archiveDirPath);
    }

    @Scheduled(fixedDelay = 60000)
    public void ingestCsvFiles() {
        System.out.println("Scheduler running at " + new Date());
        try (DirectoryStream<Path> files = Files.newDirectoryStream(uploadDir, "*.csv")) {
            for (Path file : files) {
                processFile(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Path createDirectory(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return path;
    }

    void processFile(Path file) throws IOException {
        System.out.println("Processing file: " + file.toAbsolutePath());
        try (Reader reader = Files.newBufferedReader(file);
             CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim().withIgnoreEmptyLines())) {

            List<Mono<Void>> tasks = new ArrayList<>();
            for (CSVRecord record : parser) {
                ProductDTO dto = mapRecordToProduct(record);
                if (isValidProduct(dto)) {
                    tasks.add(processProduct(dto));
                } else {
                    System.out.println("Skipping invalid product: " + dto);
                }
            }
            Mono.when(tasks).block();
        }
        Files.move(file, archiveDir.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
    }

    ProductDTO mapRecordToProduct(CSVRecord record) {
        ProductDTO dto = new ProductDTO();
        dto.setName(record.get("name"));
        dto.setBrand(record.get("brand"));
        dto.setPrice(parsePrice(record.get("price")));
        dto.setCamera(record.isMapped("camera") ? record.get("camera") : null);
        dto.setBattery(record.isMapped("battery") ? record.get("battery") : null);
        dto.setDisplayType(record.isMapped("displaytype") ? record.get("displaytype") : null);
        dto.setDescription(record.isMapped("description") ? record.get("description") : null);
        return dto;
    }

    Double parsePrice(String priceStr) {
        try {
            return (priceStr != null && !priceStr.isEmpty()) ? Double.valueOf(priceStr) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    boolean isValidProduct(ProductDTO dto) {
        return dto.getName() != null && !dto.getName().isEmpty() &&
                dto.getBrand() != null && !dto.getBrand().isEmpty();
    }

    Mono<Void> processProduct(ProductDTO dto) {
        if (dto.getDescription() == null || dto.getDescription().length() < 20) {
            String prompt = generatePrompt(dto);
            return openAiService.generateDescription(prompt)
                    .flatMap(description -> {
                        if (description != null && !description.isEmpty()) {
                            dto.setDescription(description);
                        }
                        return productService.createProduct(dto);
                    }).then();
        }
        return productService.createProduct(dto).then();
    }

    private String generatePrompt(ProductDTO dto) {
        return "Write a detailed, catchy product description for: " +
                dto.getBrand() + " " + dto.getName() +
                " priced at " + dto.getPrice() +
                " with features: " + dto.getCamera() + ", " +
                dto.getBattery() + ", display " + dto.getDisplayType();
    }
}
