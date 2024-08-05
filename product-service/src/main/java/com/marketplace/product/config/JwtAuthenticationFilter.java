package com.marketplace.product.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

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
			
			
			System.out.println("Use header :: " + jwt + "userEmail" + userEmail );

			if (userEmail != null) {

				try {

					ResponseEntity<VerifyTokenResponse> verifyTokenResponse = restTemplate.getForEntity(authUrl + jwt,
							VerifyTokenResponse.class);
					VerifyTokenResponse verifyToken = verifyTokenResponse.getBody();

					System.out.println("verifyTokenResponse : " + verifyTokenResponse.toString());

					setSecurityContext(verifyToken.getEmail(), verifyToken.getRoles(), request);

				} catch (Exception e) {
					System.out.println("Rest Template error : ********************" + e.toString());
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
}