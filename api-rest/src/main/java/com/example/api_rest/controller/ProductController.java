package com.example.api_rest.controller;

import com.example.api_rest.dto.ProductDetail;
import com.example.api_rest.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductDetail>> getSimilarProducts(@PathVariable String productId) {
        try {
            List<ProductDetail> similarProducts = service.getSimilarProducts(productId);
            return ResponseEntity.ok(similarProducts);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
