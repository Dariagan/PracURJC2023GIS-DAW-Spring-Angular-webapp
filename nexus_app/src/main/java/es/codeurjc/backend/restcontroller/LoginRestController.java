package es.codeurjc.backend.restcontroller;

import es.codeurjc.backend.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LoginRestController
{
    @Autowired
    private LoginService loginService;

    /**
     * @return JWT base64 encoded as String
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
        @RequestParam String username,
        @RequestParam String password
    ) {
        return loginService.doLoginFor(username, password);
    }
}
