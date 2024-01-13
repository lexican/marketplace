package com.marketplace.product.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserForbiddenErrorHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	public UserForbiddenErrorHandler() {
		this.objectMapper = new ObjectMapper();
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
			throws IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);

		Map<String, Object> data = new HashMap<>();
		data.put("status", "Failure");
		data.put("timestamp", Calendar.getInstance().getTime());
		data.put("exception", ex.getMessage());

		System.out.println("Error message : " + data.toString());

		response.getOutputStream().println(objectMapper.writeValueAsString(data));
	}
}