package com.marketplace.product.config;

import java.io.IOException;
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
		final String authHeader = request.getHeader("Authorization");
		
		final String jwt;
		final String userEmail;

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		jwt = authHeader.substring(7);
		userEmail = jwtService.extractUsername(jwt);
		
		//&& SecurityContextHolder.getContext().getAuthentication().toString() == null

		if (userEmail != null ) {
			
			try {
				
				
				ResponseEntity<VerifyTokenResponse> verifyTokenResponse = restTemplate.getForEntity(authUrl + jwt, VerifyTokenResponse.class);
				VerifyTokenResponse verifyToken = verifyTokenResponse.getBody();
				
				System.out.println("verifyTokenResponse : " + verifyTokenResponse.toString());
				
				List<GrantedAuthority> authorities = verifyToken.getRoles().stream()
						.map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
				
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(verifyToken.getEmail(), null, authorities);

				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authToken);
				
				
			}catch(Exception e) {
				System.out.println("Rest Template error : ********************" + e.toString());
			}

		}
		
		filterChain.doFilter(request, response);
	}
}