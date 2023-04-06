package es.codeurjc.backend.controller;

import es.codeurjc.backend.utilities.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.backend.model.User;
import es.codeurjc.backend.service.UserService;

// Entire controller programmed by group 13 A
@RestController
@RequestMapping("/api/auth")
public class SignUpController
{
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // FIXME this endpoints returns the signup page on URI /api/auth/signup,
    // but it should get returned on simply /signup
    @GetMapping("/signup")
    public String showSignUpForm(Model model)
    {
        return "signuppage";
    }

    @PostMapping("/signup")
    public ResponseEntity<?> processSignUpForm(
        @RequestParam String email,
        @RequestParam String username,
        @RequestParam String password
    ) {
        if (!emailIsValid(email))
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

    private boolean emailIsValid(String email)
    {
        return UserService.isEmail(email) && !userService.isEmailTaken(email);
    }

    // TODO
    private void processEmailVerification(User newUser)
    {

    }
}