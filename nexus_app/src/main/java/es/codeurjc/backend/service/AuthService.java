package es.codeurjc.backend.service;

import es.codeurjc.backend.model.User;
import es.codeurjc.backend.utilities.responseentity.ResponseBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import es.codeurjc.backend.security.JwtService;
import es.codeurjc.backend.utilities.AuthResponse;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService
{
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    
    public ResponseEntity<?> register(
    String email, String username, String password
    ) {
        if (!emailService.emailIsValid(email))
            return ResponseBuilder.badReq("Wrong email provided");
        
        if (userService.isUsernameTaken(username))
            return ResponseBuilder.badReq("Username already in use");
        
        // NOTE not production ready code
        if (password.length() < 1)
            return ResponseBuilder.badReq("Password too short");
        
        User newUser = userService.buildHelper(username, email, passwordEncoder.encode(password));
        userService.save(newUser);
        processEmailVerification(newUser);
        
        return ResponseEntity.ok(
            new AuthResponse(JwtService.generateToken(newUser))
        );
    }
    
    // TODO
    private void processEmailVerification(User newUser)
    {
    
    }
    
    public ResponseEntity<?> auth(String username, String password)
    {
        Optional<User> user = userService.getUserBy(username);
        if (user.isEmpty()) return ResponseBuilder.badReq("No such user");
        
        Try<Authentication> auth = Try.of(() -> authManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        ));
        
        if (auth.isFailure()) return ResponseBuilder.badReq("Wrong credentials");
        
        String jwt = JwtService.generateToken(user.get());
        
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
    
}
