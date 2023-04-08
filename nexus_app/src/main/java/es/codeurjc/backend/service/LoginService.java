package es.codeurjc.backend.service;

import es.codeurjc.backend.model.User;
import es.codeurjc.backend.security.JwtService;
import es.codeurjc.backend.utilities.AuthResponse;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import es.codeurjc.backend.utilities.responseentity.ResponseBuilder;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LoginService
{
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    // TODO
    public ResponseEntity<?> doLoginFor(String username, String password)
    {
        Optional<User> user = userService.getUserBy(username);
        if (user.isEmpty()) return ResponseBuilder.badReq("No such user");

        Try<Authentication> auth = Try.of(() -> authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        ));

        if (auth.isFailure()) return ResponseBuilder.badReq("Wrong credentials");

        String jwt = JwtService.generateToken(user.get());

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
