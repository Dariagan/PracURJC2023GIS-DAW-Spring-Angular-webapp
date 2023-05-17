package es.codeurjc.nexusapp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.codeurjc.nexusapp.model.Role;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig
{
    // TODO define all endpoints
    private final String[] PUBLIC_ENDPOINTS = {
        "**",//FIXME added this bc otherwise css GET requests get blocked
        "/api/auth/**",
        "/api/ex/public-str",
        "/api/**",//FIXME
        "/h2-console/**",
        "/login",
        "/signup",
        "/feed",
        "/u/**"
    };
    
    private final String[] USER_ENDPOINTS = {
        "/api/ex/user-str",
        "/api/ex/name"
    };
    
    private final String[] ADMIN_ENDPOINTS = {
        "/api/ex/admin-str"
    };
    
    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http.csrf()
            .disable()
            .authorizeHttpRequests()
            .antMatchers(PUBLIC_ENDPOINTS)
            .permitAll()
            .antMatchers(USER_ENDPOINTS)
            .hasAnyAuthority(Role.USER.toString(), Role.ADMIN.toString())
            .antMatchers(ADMIN_ENDPOINTS)
            .hasAuthority(Role.ADMIN.toString())
            .anyRequest().authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}