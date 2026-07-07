package co.gov.educacionbogota.sicobertura.busquedaactiva.configuration;

import co.gov.educacionbogota.sicobertura.repository.EndpointPublicoRepository;
import co.gov.educacionbogota.sicobertura.security.config.PermissionProperties;
import co.gov.educacionbogota.sicobertura.security.matcher.DatabasePublicEndpointMatcher;
import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Whitelist Capa 1 gestionada desde tabla BD `endpoint_publico` (msv_codigo=busqueda-activa).
 * Cambios en whitelist NO requieren redeploy — usar POST /api/admin/whitelist/refresh.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String MSV_CODIGO = "busqueda-activa";

    private static final List<String[]> FALLBACK_PATTERNS = Arrays.asList(
            new String[]{"/actuator/health", "*"},
            new String[]{"/actuator/info", "*"},
            new String[]{"/error", "*"},
            new String[]{"/swagger-ui/**", "GET"},
            new String[]{"/swagger-ui.html", "GET"},
            new String[]{"/swagger-resources/**", "GET"},
            new String[]{"/v3/api-docs/**", "GET"},
            new String[]{"/v2/api-docs", "GET"},
            new String[]{"/webjars/**", "GET"}
    );

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PermissionProperties permissionProperties;
    private final EndpointPublicoRepository endpointPublicoRepository;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          PermissionProperties permissionProperties,
                          EndpointPublicoRepository endpointPublicoRepository) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.permissionProperties = permissionProperties;
        this.endpointPublicoRepository = endpointPublicoRepository;
    }

    @Bean
    public DatabasePublicEndpointMatcher publicEndpointMatcher() {
        return new DatabasePublicEndpointMatcher(
                endpointPublicoRepository,
                MSV_CODIGO,
                FALLBACK_PATTERNS
        );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        if (!permissionProperties.isEnabled()) {
            http
                    .cors().and()
                    .csrf().disable()
                    .authorizeRequests()
                    .anyRequest().permitAll();
            return;
        }

        http
                .cors().and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .requestMatchers(publicEndpointMatcher()).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
