package co.gov.educacionbogota.sicobertura.busquedaactiva.configuration;

import co.gov.educacionbogota.sicobertura.dto.UsuarioAutenticado;
import co.gov.educacionbogota.sicobertura.util.JwtUtil;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            if (jwtUtil.validateToken(token)) {

                Claims claims = jwtUtil.extractAllClaims(token);

                UsuarioAutenticado usuario = new UsuarioAutenticado(
                        claims.getSubject(),
                        claims.get("correo", String.class),
                        claims.get("nombres", String.class),
                        claims.get("rol", String.class)
                );

                UsernamePasswordAuthenticationToken authentication
                        = new UsernamePasswordAuthenticationToken(
                                usuario,
                                null,
                                Collections.emptyList()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
