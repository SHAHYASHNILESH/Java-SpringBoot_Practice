package com.batch_process.demo.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.batch_process.demo.entity.Product;

public class ProductRowMapper implements RowMapper<Product> {

	@Override
	public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
		System.out.println("Result Set:-" + rs);
		Product product = new Product();
		product.setProductId(rs.getInt("product_id"));
		product.setProductName(rs.getString("product_name"));
		product.setProductCategory(rs.getString("product_category"));
		product.setProductPrice(rs.getInt("product_price"));
		return product;
	}

}
