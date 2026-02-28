package com.batch_process.demo.processor;

import org.springframework.batch.item.ItemProcessor;

import com.batch_process.demo.dto.ProductDTO;

public class FilterProductItemProcessor implements ItemProcessor<ProductDTO, ProductDTO> {

	@Override
	public ProductDTO process(ProductDTO item) throws Exception {
		// TODO Auto-generated method stub
		if (item.getProductPrice() > 1000) {
			System.out.println(item);
			return item;
		}
		return null;
	}

}
