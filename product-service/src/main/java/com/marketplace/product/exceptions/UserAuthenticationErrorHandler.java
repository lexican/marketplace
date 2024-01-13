package com.marketplace.product.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserAuthenticationErrorHandler extends BasicAuthenticationEntryPoint {
	private final ObjectMapper objectMapper;

	public UserAuthenticationErrorHandler() {
		this.objectMapper = new ObjectMapper();
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
			throws IOException {

		final PrintWriter writer = response.getWriter();

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		Map<String, Object> data = new HashMap<>();
		data.put("status", "Failure");
		data.put("timestamp", Calendar.getInstance().getTime());
		data.put("exception", ex.getMessage());

		System.out.println("Error message : " + data.toString());

		writer.println(objectMapper.writeValueAsString(data));

	}

}