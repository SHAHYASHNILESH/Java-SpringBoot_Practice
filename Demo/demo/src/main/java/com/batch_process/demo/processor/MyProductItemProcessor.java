package com.batch_process.demo.processor;

import org.springframework.batch.item.ItemProcessor;

import com.batch_process.demo.dto.ProductDTO;

public class MyProductItemProcessor implements ItemProcessor<ProductDTO, ProductDTO> {

	@Override
	public ProductDTO process(ProductDTO item) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("processor executed!!");
		item.setProductPrice((int) (item.getProductPrice() - (0.1 * item.getProductPrice())));
		return item;
	}

}
