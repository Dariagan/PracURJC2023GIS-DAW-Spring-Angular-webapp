package es.codeurjc.backend.controller;

import es.codeurjc.backend.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class LoginRestController
{
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public void login(
        @RequestParam String username,
        @RequestParam String password
    ) {
        loginService.doLoginFor(username, password);
    }
}
