package co.gov.educacionbogota.sicobertura.busquedaactiva.configuration;

import co.gov.educacionbogota.sicobertura.security.config.PermissionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PermissionProperties permissionProperties;

    private boolean securityEnabled;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
             PermissionProperties permissionProperties) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.permissionProperties = permissionProperties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        securityEnabled = permissionProperties.isEnabled();
        if (!securityEnabled) {
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
                .antMatchers(
                        "/auth/**",
                        "/v2/api-docs",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-ui/index.html",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/configuration/ui",
                        "/configuration/security"
                ).permitAll()
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
