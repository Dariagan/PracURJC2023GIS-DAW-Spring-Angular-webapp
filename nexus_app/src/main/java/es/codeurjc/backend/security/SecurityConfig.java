package es.codeurjc.backend.security;

import es.codeurjc.backend.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig
{
    // TODO define all endpoints
    private final String[] authRequiredEndpoints = {
        "api/tweets/me"
    };

    private final String[] publicEndpoints = {
        "api/login",
        "api/signup",
        "api/tweets/{id}"
    };

    private final String[] adminEndpoints = {
        "api/admin/feed"
    };

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http
            .csrf()
            .disable()
            .authorizeHttpRequests()
            .antMatchers(publicEndpoints)
            .permitAll() // Permit all public endpoints
            .anyRequest()
            .authenticated() // Any other request requires authentication
            .and()
            .authorizeHttpRequests()
            .antMatchers(adminEndpoints)
            .hasRole(Role.ADMIN.toString());

        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
