package com.batch_process.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OSProductDTO extends ProductDTO {
	private Integer taxPercent;
	private String sku;
	private Integer shippingRate;
}
