package es.codeurjc.nexusapp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
public class SecurityConfig {
    // TODO define all endpoints
    private final String[] PUBLIC_GET_ENDPOINTS = {
        
        "/api/auth/**",
        "/api/ex/public-str",
        "/api/**", // FIXME
        "/h2-console/**",
        "/login",
        "/signup",
        "/feed",
        "/u/**"
    };
    
    private final String[] USER_GET_ENDPOINTS = {
        "/api/ex/user-str",
        "/api/ex/name"
    };
    
    private final String[] USER_POST_ENDPOINTS = {
    };
    
    private final String[] ADMIN_GET_ENDPOINTS = {
        "/api/tweets/reports"
    };
    
    private final String[] ADMIN_POST_ENDPOINTS = {
    };
    
    private final JwtRequestFilter jwtRequestFilter;
    private final AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf()
            .disable()
            .authorizeHttpRequests()
            .antMatchers("/assets/css/**")
            .permitAll()
            .antMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS)
            .permitAll()
            .antMatchers(HttpMethod.GET, USER_GET_ENDPOINTS)
            .hasAnyAuthority(Role.USER.toString(), Role.ADMIN.toString())
            .antMatchers(HttpMethod.GET, ADMIN_GET_ENDPOINTS)
            .hasAuthority(Role.ADMIN.toString())
            .antMatchers(HttpMethod.POST, USER_POST_ENDPOINTS)
            .hasAnyAuthority(Role.USER.toString(), Role.ADMIN.toString())
            .antMatchers(HttpMethod.POST, ADMIN_POST_ENDPOINTS)
            .hasAuthority(Role.ADMIN.toString())
            .anyRequest().authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

