package es.codeurjc.backend.controller.routes;

import es.codeurjc.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    
    @PostMapping("/")
    public ResponseEntity<?> auth(
        @RequestParam String username,
        @RequestParam String password
    ) {
        return authService.auth(username, password);
    }
}
