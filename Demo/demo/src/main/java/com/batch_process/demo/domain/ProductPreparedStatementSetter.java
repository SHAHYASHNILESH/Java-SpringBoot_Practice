package com.batch_process.demo.domain;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import com.batch_process.demo.dto.ProductDTO;

public class ProductPreparedStatementSetter implements ItemPreparedStatementSetter<ProductDTO> {

	@Override
	public void setValues(ProductDTO item, PreparedStatement ps) throws SQLException {
		// TODO Auto-generated method stub
		ps.setInt(1, item.getProductId());
		ps.setString(2, item.getProductName());
		ps.setString(3, item.getProductCategory());
		ps.setInt(4, item.getProductPrice());
	}

}
