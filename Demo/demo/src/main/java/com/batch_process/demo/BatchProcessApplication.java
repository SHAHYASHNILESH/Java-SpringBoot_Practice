package com.batch_process.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableBatchProcessing
public class BatchProcessApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchProcessApplication.class, args);
        System.out.println("running....");
    }

}
