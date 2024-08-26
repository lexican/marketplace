package com.marketplace.product.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
	private String imageUrl;
	@Positive
	private Double price;
	@Positive
	private int quantity;
	@PositiveOrZero
	private double discount;
	@Positive
	private int categoryId;
	private String description;
	private String sku;

}