package es.codeurjc.backend.service;

import es.codeurjc.backend.model.User;
import es.codeurjc.backend.utilities.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignUpService
{
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> doSignUpFor(
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

        return ResponseEntity.ok().body("Account registered successfully");
    }

    // TODO
    private void processEmailVerification(User newUser)
    {

    }
}
