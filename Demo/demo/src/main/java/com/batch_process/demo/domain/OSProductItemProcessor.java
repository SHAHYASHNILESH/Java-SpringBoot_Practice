package com.batch_process.demo.domain;

import org.springframework.batch.item.ItemProcessor;

import com.batch_process.demo.dto.OSProductDTO;
import com.batch_process.demo.dto.ProductDTO;

public class OSProductItemProcessor implements ItemProcessor<ProductDTO, OSProductDTO> {

	@Override
	public OSProductDTO process(ProductDTO item) throws Exception {
		OSProductDTO osProductDTO = new OSProductDTO();
		osProductDTO.setProductId(item.getProductId());
		osProductDTO.setProductName(item.getProductName());
		osProductDTO.setProductCategory(item.getProductCategory());
		osProductDTO.setProductPrice(item.getProductPrice());
		osProductDTO.setTaxPercent(item.getProductCategory().equals("Sports Accessories") ? 5 : 18);
		osProductDTO.setSku(item.getProductCategory().substring(0, 3) + item.getProductId());
		osProductDTO.setShippingRate(item.getProductPrice() < 1000 ? 75 : 0);
		return osProductDTO;
	}

}
