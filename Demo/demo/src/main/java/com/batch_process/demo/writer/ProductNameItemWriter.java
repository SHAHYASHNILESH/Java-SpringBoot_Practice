package com.batch_process.demo.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import com.batch_process.demo.entity.Product;

public class ProductNameItemWriter implements ItemWriter<Product>{

	@Override
	public void write(Chunk<? extends Product> chunk) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Chunk Processing Started ====>");
		chunk.forEach(System.out::println);
		System.out.println("Chunk Processing Ended <====");
	}

}
