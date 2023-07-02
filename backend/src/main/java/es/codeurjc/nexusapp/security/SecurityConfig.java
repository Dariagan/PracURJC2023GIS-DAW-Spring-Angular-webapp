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
        
        "**",
        "/assets/css/**",
        "/api/auth/**",
        "/api/ex/public-str",
        "/api/tweets/likes**",
        "/api/users/*",
        "/api/tweets/*",
        "/h2-console/**",//FIXME
        "/login",
        "/signup",
        "/feed/**",
        "/u/**"
    };
    private final String[] PUBLIC_POST_ENDPOINTS = {
        
        "/api/auth/**",

    };
    private final String[] USER_GET_ENDPOINTS = {
        "/api/ex/user-str",
        "/api/ex/name"

    };
    private final String[] USER_POST_ENDPOINTS = {
        "/api/users**",
        "/api/users/*/image",
        "/api/tweets/*/image",
        "/api/tweets**"
    };
    private final String[] USER_DELETE_ENDPOINTS = {
        "/api/users/*",//needed for deleting own account, backend restcontroller checks if allowed
        
        "/api/users/*/blocks",
        "/api/users/*/following",
        "/api/users/*/followers",
        "/api/tweets/*/likes",
    };
    private final String[] ADMIN_GET_ENDPOINTS = {
        "/api/tweets/*/reports"
    };
    private final String[] ADMIN_POST_ENDPOINTS = {
    };
    private final String[] ADMIN_DELETE_ENDPOINTS = {
    };
    
    private final JwtRequestFilter jwtRequestFilter;
    private final AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf()
            .disable()
            .authorizeHttpRequests()
            .antMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS)
            .permitAll()
            .antMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS)
            .permitAll()
            .antMatchers(HttpMethod.GET, USER_GET_ENDPOINTS)
            .hasAnyAuthority(Role.USER.toString(), Role.ADMIN.toString())
            .antMatchers(HttpMethod.POST, USER_POST_ENDPOINTS)
    
            .hasAnyAuthority(Role.USER.toString(), Role.ADMIN.toString())
            .antMatchers(HttpMethod.DELETE, USER_DELETE_ENDPOINTS)
            .hasAnyAuthority(Role.USER.toString(), Role.ADMIN.toString())
            .antMatchers(HttpMethod.GET, ADMIN_GET_ENDPOINTS)
            .hasAuthority(Role.ADMIN.toString())
            .antMatchers(HttpMethod.POST, ADMIN_POST_ENDPOINTS)
            .hasAuthority(Role.ADMIN.toString())
            .antMatchers(HttpMethod.DELETE, ADMIN_DELETE_ENDPOINTS)
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

