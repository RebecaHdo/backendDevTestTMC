package com.example.api_rest.service;

import com.example.api_rest.dto.ProductDetail;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
public class ProductService {

    private final WebClient webClient;

    @Value("${external.api.base:http://localhost:3001}")
    private String EXTERNAL_API_BASE;

    public ProductService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(EXTERNAL_API_BASE).build();
    }

    public List<ProductDetail> getSimilarProducts(String productId) {
        try {
            String[] similarIds = webClient
                    .get()
                    .uri("/product/{id}/similarids", productId)
                    .retrieve()
                    .bodyToMono(String[].class)
                    .block();

            if (similarIds == null) return List.of();

            return Flux.fromArray(similarIds)
                    .flatMap(id ->
                            webClient.get()
                                    .uri("/product/{id}", id)
                                    .retrieve()
                                    .bodyToMono(ProductDetail.class)
                                    .onErrorResume(WebClientResponseException.NotFound.class, e -> Mono.empty())
                    )
                    .collectList()
                    .block();

        } catch (WebClientResponseException.NotFound e) {
            throw new RuntimeException("Product not found", e);
        }
    }
}