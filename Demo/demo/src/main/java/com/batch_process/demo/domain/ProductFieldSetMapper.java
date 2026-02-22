package com.batch_process.demo.domain;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.batch_process.demo.entity.Product;

public class ProductFieldSetMapper implements FieldSetMapper<Product> {

	@Override
	public Product mapFieldSet(FieldSet fieldSet) throws BindException {
		Product product = new Product();
		product.setProductId(fieldSet.readInt("product_id"));
		product.setProductName(fieldSet.readString("product_name"));
		product.setProductCategory(fieldSet.readString("product_category"));
		product.setProductPrice(fieldSet.readInt("product_price"));
		return product;
	}

}
