package com.example.systeme_reservation_jo_backend.security;

import com.example.systeme_reservation_jo_backend.service.UtilisateurDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Profile("!test")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UtilisateurDetailsServiceImpl utilisateurDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Ignore les requêtes de type OPTIONS (préflight CORS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        String requestURI = request.getRequestURI();
        logger.info("URI reçue : " + requestURI);

        // Bypass : si l'URI contient /api/auth/, ne pas traiter le JWT
        if (requestURI.contains("/api/auth/")) {
            logger.info("Bypass du JWT filter pour l'URI : " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extraction du token JWT depuis l'en-tête Authorization
            String jwt = getJwtFromRequest(request);
            if (jwt == null) {
                logger.warn("Aucun token JWT trouvé dans la requête.");
            } else {
                logger.info("Token brut reçu : " + jwt);
            }

            // Si le token est présent et valide, établir l'authentication
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Extraction de l'email depuis le token
                String email = tokenProvider.getUsernameFromJWT(jwt);
                logger.info("Email extrait du token : " + email);

                // Chargement des détails de l'utilisateur
                UserDetails userDetails = utilisateurDetailsService.loadUserByUsername(email);
                logger.info("Authorities récupérées pour l'utilisateur : " + userDetails.getAuthorities());

                // Création de l'objet d'authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Stockage de l'authentication dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Authentification définie dans SecurityContext pour : " + email);
            } else {
                logger.warn("Le token est invalide ou vide.");
            }
        } catch (Exception ex) {
            logger.error("Erreur lors du traitement du JWT : ", ex);
        }

        // Poursuite de la chaîne des filtres
        filterChain.doFilter(request, response);
    }

    // Extraction du token JWT depuis l'en-tête Authorization
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7); // Supprime "Bearer "
            logger.info("Token extrait après 'Bearer ': " + token);
            return token;
        }
        return null;
    }
}
