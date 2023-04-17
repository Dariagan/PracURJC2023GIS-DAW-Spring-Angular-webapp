package es.codeurjc.nexusapp.controller.routes;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.nexusapp.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {
    
    private final AuthService authService;
    
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
        @RequestParam String email,
        @RequestParam String username,
        @RequestParam String password
    ) {
        return authService.register(email, username, password);
    }
    
    @GetMapping
    public ResponseEntity<?> auth(
        @RequestParam String username,
        @RequestParam String password
    ) {
        return authService.auth(username, password);
    }
}
