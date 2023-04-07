package es.codeurjc.backend.service;

import es.codeurjc.backend.model.User;
import es.codeurjc.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmailService
{
    @Autowired
    private UserRepository userRepository;

    public static boolean isEmail(String input)
    {
        return input.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    }

    public boolean isEmailTaken(String email)
    {
        return userRepository.existsByEmail(email);
    }

    public boolean emailIsValid(String email)
    {
        return isEmail(email) && !isEmailTaken(email);
    }
}