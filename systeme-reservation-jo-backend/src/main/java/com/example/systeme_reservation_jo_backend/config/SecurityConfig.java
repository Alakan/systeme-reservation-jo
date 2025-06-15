package com.example.systeme_reservation_jo_backend.config;

import com.example.systeme_reservation_jo_backend.security.JwtAuthenticationFilter;
import com.example.systeme_reservation_jo_backend.service.UtilisateurDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
@Profile("!test")
public class SecurityConfig {

    private final UtilisateurDetailsServiceImpl utilisateurDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(UtilisateurDetailsServiceImpl utilisateurDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.utilisateurDetailsService = utilisateurDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Désactivation CSRF pour simplifier les tests et le dev
                .csrf(csrf -> csrf.disable())
                // CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Pas de session, on utilise JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Gestion des erreurs d'authentification
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authEx) -> {
                            String origin = request.getHeader("Origin");
                            if (origin != null) {
                                response.setHeader("Access-Control-Allow-Origin", origin);
                                response.setHeader("Vary", "Origin");
                                response.setHeader("Access-Control-Allow-Credentials", "true");
                            }
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("Unauthorized");
                        })
                )
                // Règles d'autorisation
                .authorizeHttpRequests(auth -> auth
                        // Autoriser les OPTIONS (preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Éléments publics
                        .requestMatchers(HttpMethod.GET, "/favicon.ico").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/").permitAll()
                        // Événements publics
                        .requestMatchers(HttpMethod.GET, "/api/evenements/**").permitAll()
                        // Événements CRUD → ADMINISTRATEUR
                        .requestMatchers(HttpMethod.POST,   "/api/evenements/**").hasRole("ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.PUT,    "/api/evenements/**").hasRole("ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.DELETE, "/api/evenements/**").hasRole("ADMINISTRATEUR")

                        // --------- CORRECTION ICI ---------
                        // Autoriser tout utilisateur authentifié à voir/mettre à jour SON profil
                        .requestMatchers(HttpMethod.GET, "/api/utilisateurs/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/utilisateurs/me").authenticated()
                        // Autorisation générique sur les autres routes utilisateurs (CRUD → ADMIN)
                        .requestMatchers(HttpMethod.GET,    "/api/utilisateurs/**").authenticated()
                        .requestMatchers(HttpMethod.PUT,    "/api/utilisateurs/**").hasRole("ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.DELETE, "/api/utilisateurs/**").hasRole("ADMINISTRATEUR")

                        // Réservations / billets / paiements (nécessite d’être authentifié)
                        .requestMatchers("/api/reservations/**").authenticated()
                        .requestMatchers("/api/billets/**").authenticated()
                        .requestMatchers("/api/paiements/**").authenticated()

                        // Endpoints d'administration complets → ADMINISTRATEUR
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRATEUR")

                        // Toute autre route est interdite
                        .anyRequest().denyAll()
                )
                // Ajout du filtre JWT avant le filtre de session standard
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://front-systeme-reservation-jo-be1e62ad3714.herokuapp.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization","Content-Type","Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }
}
