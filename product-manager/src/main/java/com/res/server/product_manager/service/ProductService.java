package com.res.server.product_manager.service;

import com.res.server.product_manager.model.Product;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.UUID;

@Service
public class ProductService {
    private List<Product> productList=new ArrayList<>();

    public Product addProduct(Product product) {
        product.setProductId(UUID.randomUUID().toString());
        productList.add(product);
        return product;
    }

    public List<Product> getProducts() {
        return productList;
    }

    public void deleteProduct(String productId) {
        productList.removeIf(product -> product.getProductId().equalsIgnoreCase(productId));
    }
}
