package com.batch_process.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
	private Integer productId;
	private String productName;
	private String productCategory;
	private Integer productPrice;
}
