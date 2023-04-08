package es.codeurjc.backend.service;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import es.codeurjc.backend.utilities.responseentity.ResponseBuilder;

@Service
@AllArgsConstructor
public class LoginService
{
    private final AuthenticationManager authenticationManager;

    // TODO
    public ResponseEntity<?> doLoginFor(String username, String password)
    {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
        return ResponseBuilder.badReq("TODO");
    }
}
