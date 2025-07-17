package com.example.api_rest.service;

import com.example.api_rest.dto.ProductDetail;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {

    private final WebClient webClient;

    private final String EXTERNAL_API_BASE = "http://localhost:3001";

    public ProductService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl(EXTERNAL_API_BASE).build();
    }

    public List<ProductDetail> getSimilarProducts(String productId) {
        try {
            String[] similarIds = webClient.get()
                    .uri("/product/{id}/similarids", productId)
                    .retrieve()
                    .bodyToMono(String[].class)
                    .block();

            if (similarIds == null) return List.of();

            return Arrays.stream(similarIds)
                    .parallel()
                    .map(this::getProductDetailSafely)
                    .filter(Objects::nonNull)
                    .toList();

        } catch (WebClientResponseException.NotFound e) {
            throw new RuntimeException("Product not found");
        }
    }

    @Cacheable("products")
    public ProductDetail getProductDetail(String id) {
        return webClient.get()
                .uri("/product/{id}", id)
                .retrieve()
                .bodyToMono(ProductDetail.class)
                .block();
    }

    private ProductDetail getProductDetailSafely(String id) {
        try {
            return getProductDetail(id);
        } catch (WebClientResponseException.NotFound e) {
            return null;
        }
    }
}