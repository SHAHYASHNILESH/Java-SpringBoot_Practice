package com.res.server.product_manager.controller;

import com.res.server.product_manager.model.Product;
import com.res.server.product_manager.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @GetMapping
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    @DeleteMapping("/{id}")
    public void deleteProducts(@PathVariable String id) {
        productService.deleteProduct(id);
    }
}
