package com.batch_process.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.batch_process.demo.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}