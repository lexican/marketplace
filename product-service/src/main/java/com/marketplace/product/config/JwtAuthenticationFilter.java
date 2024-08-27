package com.marketplace.product.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.product.exceptions.CustomException;
import com.marketplace.product.payload.response.VerifyTokenResponse;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final RestTemplate restTemplate;
	private ObjectMapper objectMapper = new ObjectMapper();

	@Value("${app.auth.url}")
	private String authUrl;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		//
		String authHeader = request.getHeader("Authorization");
		String email = request.getHeader("email");
		String roles = request.getHeader("roles");

		// Return if authorization token is not present
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		final String jwt = authHeader.substring(7);

		if (email != null && roles != null) {
			List<String> rolesList = Arrays.asList(roles.split(","));

			String userEmail = jwtService.extractUsername(jwt);

			if (userEmail != null && (email.equals(userEmail))) {
				System.out.println("email == username");
				setSecurityContext(email, rolesList, request);

			} else {
				System.out.println("email != username");
				filterChain.doFilter(request, response);
				return;
			}

		} else {

			String userEmail = jwtService.extractUsername(jwt);

			System.out.println("Use header :: " + jwt + "userEmail" + userEmail);

			if (userEmail != null) {

				try {

					ResponseEntity<String> restResponse = restTemplate.getForEntity(authUrl + jwt, String.class);

					String body = restResponse.getBody();

					VerifyTokenResponse verifyTokenResponse = VerifyTokenResponse.builder().build();
					try {
						verifyTokenResponse = objectMapper.readValue(body, new TypeReference<VerifyTokenResponse>() {
						});
						setSecurityContext(verifyTokenResponse.getData().getEmail(),
								verifyTokenResponse.getData().getRoles(), request);
						System.out.println("verifyTokenResponse : " + verifyTokenResponse.toString());
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}

				} catch (Exception e) {

					final PrintWriter writer = response.getWriter();

					response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");

					Map<String, Object> data = new HashMap<>();
					data.put("success", false);
					data.put("timestamp", Calendar.getInstance().getTime());
					data.put("error", e.toString());

					writer.println(objectMapper.writeValueAsString(data));

					return;
				}

			}
		}

		filterChain.doFilter(request, response);
	}

	private void setSecurityContext(String userEmail, List<String> roles, HttpServletRequest request) {
		List<GrantedAuthority> authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role))
				.collect(Collectors.toList());

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userEmail, null,
				authorities);

		authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

		SecurityContextHolder.getContext().setAuthentication(authToken);
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager();
	}
}