package com.marketplace.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.marketplace.auth.exceptions.CustomAuthenticationFailureHandler;
import com.marketplace.auth.exceptions.UserAuthenticationErrorHandler;
import com.marketplace.auth.exceptions.UserForbiddenErrorHandler;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

	private final JwtAuthenticationFilter jwtAuthFilter;
	private final AuthenticationProvider authenticationProvider;

	@SuppressWarnings("removal")
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/auth/login").permitAll()
						.requestMatchers("/api/auth/register").permitAll().requestMatchers("/api/auth/validate")
						.permitAll().anyRequest().authenticated())

				.formLogin(formLogin -> formLogin.loginPage("/login").permitAll())
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
				.exceptionHandling(ex -> ex.authenticationEntryPoint(userAuthenticationErrorHandler())
						.accessDeniedHandler(new UserForbiddenErrorHandler()));

		return http.build();
	}

	@Bean
	public AuthenticationEntryPoint userAuthenticationErrorHandler() {
		UserAuthenticationErrorHandler userAuthenticationErrorHandler = new UserAuthenticationErrorHandler();
		userAuthenticationErrorHandler.setRealmName("Basic Authentication");
		return userAuthenticationErrorHandler;
	}

}
