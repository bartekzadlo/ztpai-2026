package pl.edu.pk.gamelibrary.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Brak tokenu -> przepuść, Security zwróci 401 przez authenticationEntryPoint
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            // Token istnieje ale nieważny/wygasły -> 403
            // Piszemy bezpośrednio do response żeby uniknąć przekierowania przez /error
            writeError(response, HttpServletResponse.SC_FORBIDDEN, "Invalid or expired token");
            return;
        }

        // Token prawidłowy - ustaw authentication
        // Spring Security sprawdzi rolę: jeśli zła -> accessDeniedHandler -> 403
        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);

        var authority = new SimpleGrantedAuthority("ROLE_" + role);
        var auth = new UsernamePasswordAuthenticationToken(
                username, null, List.of(authority));
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String body = objectMapper.writeValueAsString(Map.of(
                "status", status,
                "message", message
        ));
        response.getWriter().write(body);
    }
}