package com.marketplace.product.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.marketplace.product.payload.response.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ResponseEntity<Object> handleGeneralException(Exception ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.builder().success(false).data(null)
				.message("An unexpected error occurred " + ex.getMessage()).build());
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.builder().success(false).data(null).message(ex.getMessage()).build());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		String error = "Invalid path variable: " + ex.getMessage();
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.builder().success(false).data(null).message(error).build());
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Object> handleMethodNotSupportedException() {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.builder().success(false).data(null).message("End point not found").build());
	}

}
