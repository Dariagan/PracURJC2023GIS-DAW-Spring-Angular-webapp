package es.codeurjc.backend.security;

import es.codeurjc.backend.service.UserService;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter
{
    private final UserService userService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        Option<String> authHeader = Option.of(request.getHeader("Authorization"));

        if (shouldIgnoreRequest(authHeader))
        {
            filterChain.doFilter(request, response);
            return;
        }

        Option<String> jwt = authHeader.flatMap(this::getJwtFromHeader);
        Option<String> username = jwt.map(JwtService::extractUsername);

        if (username.isDefined() && !JwtService.userAlreadyAuthenticated())
        {
            Optional<UserDetails> userDetails = username
                .map(userService::getUserDetailsBy)
                .get();

            if (JwtService.isTokenValid(jwt.toJavaOptional(), userDetails))
                updateAuthAfterJwtVerification(userDetails.get(), request);

        }
        filterChain.doFilter(request, response);
    }

    /**
     * Checks if the auth header starts with "Bearer ", which defines the JWT
     * auth type. If it doesn't, or if the header doesn't exist, the request
     * should get discarded
     * @param authHeader the header of the JWT
     * @return           if the request should get ignored
     */
    private boolean shouldIgnoreRequest(Option<String> authHeader)
    {
        return authHeader.isEmpty() || !authHeader.get().startsWith("Bearer ");
    }

    /**
     * Returns the JWT whole String from the auth header of a request. Note
     * that it does so by slicing the first 7 characters, because
     * len("Bearer ") == 7 (see `shouldIgnoreRequest`).
     */
    private Option<String> getJwtFromHeader(String authHeader)
    {
        return Try.of(() -> authHeader.substring(7)).toOption();
    }

    private void updateAuthAfterJwtVerification(UserDetails userDetails, HttpServletRequest req)
    {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
