package com.batch_process.demo.loader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.batch_process.demo.entity.Product;
import com.batch_process.demo.repository.ProductRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(ProductRepository repository) {
        return args -> {

            repository.save(new Product(1, "Google Pixel 8", "Mobile Phones", 1200));
            repository.save(new Product(2, "iPad Mini", "Tablets", 800));
            repository.save(new Product(3, "Canon 1500D", "Cameras", 500));
            repository.save(new Product(4, "LG 4K Ultra HD TV", "Televisions", 300));
            repository.save(new Product(5, "Goalkeeper Gloves", "Sports Accessories", 100));

            System.out.println("Products loaded successfully!");
        };
    }
}
