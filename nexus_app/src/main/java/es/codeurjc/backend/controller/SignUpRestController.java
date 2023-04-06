package es.codeurjc.backend.controller;

import es.codeurjc.backend.service.EmailService;
import es.codeurjc.backend.utilities.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.backend.model.User;
import es.codeurjc.backend.service.UserService;

// Entire controller programmed by group 13 A
@RestController
@RequestMapping("/api/auth")
public class SignUpRestController
{
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> processSignUpForm(
        @RequestParam String email,
        @RequestParam String username,
        @RequestParam String password
    ) {
        if (!emailService.emailIsValid(email))
            return ResponseBuilder.badReq("Wrong email provided");

        if (userService.isUsernameTaken(username))
            return ResponseBuilder.badReq("Username already in use");

        // NOTE not production ready code
        if (password.length() == 0)
            return ResponseBuilder.badReq("Password too short");

        User newUser = userService.buildHelper(username, email, passwordEncoder.encode(password));
        userService.save(newUser);
        processEmailVerification(newUser);

        return ResponseEntity.ok().body("Account registered successfully");
    }

    // TODO
    private void processEmailVerification(User newUser)
    {

    }
}