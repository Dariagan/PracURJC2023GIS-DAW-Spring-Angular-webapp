package es.codeurjc.backend.restcontroller;
import es.codeurjc.backend.service.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Entire controller programmed by group 13 A
@RestController
@RequestMapping("/api")
public class SignUpRestController
{
    @Autowired
    private SignUpService signUpService;

    @PostMapping("/signup")
    public ResponseEntity<?> processSignUpForm(
        @RequestParam String email,
        @RequestParam String username,
        @RequestParam String password
    ) {
        return signUpService.doSignUpFor(email, username, password);
    }
}