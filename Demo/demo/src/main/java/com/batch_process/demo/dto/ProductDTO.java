package com.batch_process.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
	private Integer productId;
	private String productName;
	@Pattern(regexp = "Mobile Phones|Tablets|Cameras|Televisions")
	private String productCategory;
	@Max(100000)
	private Integer productPrice;
}
