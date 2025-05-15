package com.example.systeme_reservation_jo.config;

import com.example.systeme_reservation_jo.security.JwtAuthenticationFilter;
import com.example.systeme_reservation_jo.service.UtilisateurDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
@Profile("!test")
public class SecurityConfig {

    private final UtilisateurDetailsServiceImpl utilisateurDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(UtilisateurDetailsServiceImpl utilisateurDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.utilisateurDetailsService = utilisateurDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Désactive CSRF pour les API REST
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configuration CORS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT Stateless
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/evenements/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/evenements/**").hasRole("ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.PUT, "/api/evenements/**").hasRole("ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.DELETE, "/api/evenements/**").hasRole("ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.GET, "/api/utilisateurs/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/utilisateurs/**").hasRole("ADMINISTRATEUR") // ✅ Autorisation pour PUT
                        .requestMatchers(HttpMethod.DELETE, "/api/utilisateurs/**").hasRole("ADMINISTRATEUR") // ✅ Ajout pour autoriser DELETE
                        .requestMatchers(HttpMethod.GET, "/api/reservations/**").hasAnyRole("ADMINISTRATEUR", "UTILISATEUR")
                        .requestMatchers(HttpMethod.POST, "/api/reservations/**").hasAnyRole("ADMINISTRATEUR", "UTILISATEUR")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservations/**").hasAnyRole("ADMINISTRATEUR", "UTILISATEUR")
                        .requestMatchers(HttpMethod.POST, "/api/billets/**").hasAnyRole("ADMINISTRATEUR", "UTILISATEUR")
                        .requestMatchers(HttpMethod.DELETE, "/api/billets/**").hasAnyRole("ADMINISTRATEUR", "UTILISATEUR")
                        .anyRequest().denyAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // 🔹 Autorise toutes les origines (à adapter en prod)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
