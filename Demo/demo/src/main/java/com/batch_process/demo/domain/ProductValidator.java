package com.batch_process.demo.domain;

import java.util.List;

import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

import com.batch_process.demo.dto.ProductDTO;

public class ProductValidator implements Validator<ProductDTO> {
	List<String> validCategories = List.of("Mobile Phones", "Tablets", "Cameras", "Televisions");

	@Override
	public void validate(ProductDTO value) throws ValidationException {
		// TODO Auto-generated method stub
		if (!validCategories.contains(value.getProductCategory()))
			throw new ValidationException("Invalid Product Category");
		if (value.getProductPrice() > 100000)
			throw new ValidationException("Invalid Product Price");

	}
}
