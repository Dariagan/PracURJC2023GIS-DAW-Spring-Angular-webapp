package es.codeurjc.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SignUpController
{
    @RequestMapping("/signup")
    public String showSignUpForm(Model model)
    {
        return "signuppage";
    }
}
