package com.th.learningenglish.config;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class SecurityConfig {

	private static final String SECRET = "12576576587689778901222222222222";

	@Bean
	public JwtDecoder jwtDecoder() {
		SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
		return NimbusJwtDecoder.withSecretKey(key).build();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/**").permitAll()
						.requestMatchers("/error").permitAll().requestMatchers("/api/payments/confirm/momo-ipn")
						.permitAll().requestMatchers("/api/payments/momo/return").permitAll()
						.requestMatchers("/api/chat").permitAll().anyRequest().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())));

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(java.util.List.of("http://localhost:3000"));
		config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(java.util.List.of("*"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public Cloudinary cloudinary() {
		return new Cloudinary(ObjectUtils.asMap("cloud_name", "dyuhodi7a", "api_key", "918856967439272", "api_secret",
				"anmwPziYC5Bg-Sq7IHZyR55EdLk", "secure", true));
	}
}