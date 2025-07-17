package com.example.api_rest.service;

import com.example.api_rest.dto.ProductDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String EXTERNAL_API_BASE = "http://localhost:3001";

    public List<ProductDetail> getSimilarProducts(String productId) {
        List<ProductDetail> result = new ArrayList<>();

        try {
            String[] similarIds = restTemplate.getForObject(
                    EXTERNAL_API_BASE + "/product/" + productId + "/similarids",
                    String[].class
            );

            if (similarIds != null) {
                for (String id : similarIds) {
                    try {
                        ProductDetail detail = restTemplate.getForObject(
                                EXTERNAL_API_BASE + "/product/" + id,
                                ProductDetail.class
                        );
                        if (detail != null) {
                            result.add(detail);
                        }
                    } catch (HttpClientErrorException.NotFound e) {
                        
                    }
                }
            }

            return result;

        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Product not found");
        }
    }
}
