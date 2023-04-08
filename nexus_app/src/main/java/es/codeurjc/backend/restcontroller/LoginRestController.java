package es.codeurjc.backend.restcontroller;

import es.codeurjc.backend.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class LoginRestController
{
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
        @RequestParam String username,
        @RequestParam String password
    ) {
        return loginService.doLoginFor(username, password);
    }
}
