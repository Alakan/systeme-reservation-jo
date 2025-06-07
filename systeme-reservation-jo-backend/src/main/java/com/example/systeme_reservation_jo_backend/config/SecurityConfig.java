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
    public SecurityConfig(UtilisateurDetailsServiceImpl utilisateurDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.utilisateurDetailsService = utilisateurDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Ajoute les en-têtes CORS même pour les réponses d'erreur
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
                .authorizeHttpRequests(auth -> auth
                        // Autoriser les préflights
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Les endpoints publics
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()

                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()

                        // Les endpoints publics pour les événements
                        .requestMatchers(HttpMethod.GET, "/api/evenements/**").permitAll()
                        // Endpoints réservés aux administrateurs pour les événements
                        .requestMatchers(HttpMethod.POST, "/api/evenements/**").hasRole("ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.PUT, "/api/evenements/**").hasRole("ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.DELETE, "/api/evenements/**").hasRole("ADMINISTRATEUR")

                        // Les endpoints liés aux utilisateurs, réservés aux authentifiés ou administrateurs
                        .requestMatchers(HttpMethod.GET, "/api/utilisateurs/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/utilisateurs/**").hasRole("ADMINISTRATEUR")
                        .requestMatchers(HttpMethod.DELETE, "/api/utilisateurs/**").hasRole("ADMINISTRATEUR")

                        // Endpoints liés aux réservations et billets / paiements (sécurisés)
                        .requestMatchers(HttpMethod.GET, "/api/reservations/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reservations/utilisateur/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/reservations/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/{id}/paiement").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/reservations/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/billets/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/billets/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/billets/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/billets/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/paiements/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/paiements/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/paiements/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/paiements/**").authenticated()
                        // Endpoints administrateur
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRATEUR")

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
        // Autoriser les origines de développement et de production
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://front-systeme-reservation-jo-be1e62ad3714.herokuapp.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Ce bean garantit que le filtre CORS est exécuté en priorité maximale.
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }
}
