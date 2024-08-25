package com.marketplace.product.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateProductRequest {
	@NotBlank
	private String name;
	@NotBlank
	private Double price;
	@NotBlank
	private int quantity;
	@NotBlank
	private int category_id;
	private String description;
	private String sku;

}