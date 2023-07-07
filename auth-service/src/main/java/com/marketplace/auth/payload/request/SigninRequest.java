package com.marketplace.auth.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class SigninRequest {
	@NotBlank
	@Size(min = 3, max = 20)
	private String email;

	@NotBlank
	@Size(min = 6, max = 50)
	private String password;

}