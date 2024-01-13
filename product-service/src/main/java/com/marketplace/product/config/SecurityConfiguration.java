package com.marketplace.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.marketplace.product.exceptions.UserAuthenticationErrorHandler;
import com.marketplace.product.exceptions.UserForbiddenErrorHandler;

import lombok.RequiredArgsConstructor;


@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
	
	private final JwtAuthenticationFilter jwtAuthFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authorize -> authorize.requestMatchers("/product/hello")
						.permitAll().anyRequest().authenticated())

				.formLogin(formLogin -> formLogin.loginPage("/login").permitAll())
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
						)
//				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
				
				.exceptionHandling(ex -> ex.authenticationEntryPoint(userAuthenticationErrorHandler())
						.accessDeniedHandler(new UserForbiddenErrorHandler())
						);

		return http.build();
	}

	@Bean
	public AuthenticationEntryPoint userAuthenticationErrorHandler() {
		UserAuthenticationErrorHandler userAuthenticationErrorHandler = new UserAuthenticationErrorHandler();
		userAuthenticationErrorHandler.setRealmName("Basic Authentication");
		return userAuthenticationErrorHandler;
	}
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}