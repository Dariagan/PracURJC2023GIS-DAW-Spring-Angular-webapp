package es.codeurjc.nexusapp.controller.deprecated;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.nexusapp.controller.rest.AuthRestController;
import es.codeurjc.nexusapp.model.User;
import es.codeurjc.nexusapp.service.UserService;
import es.codeurjc.nexusapp.utilities.LoginRequest;


// Entire controller programmed by group 13 A
@Controller
public class SignUpController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthRestController authRestController;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        return "signuppage";
    }
   
    @PostMapping("/signup")
    public String processSignUpForm(@RequestParam String email, @RequestParam String username, @RequestParam String password, 
    HttpServletRequest req, HttpServletResponse res, Model model) {
        
        //if (!UserService.isEmail(email)) 

            //model.addAttribute("fail", "E-mail format not adequate.");
        
        //if (userService.isEmailTaken(email)) 

          //  model.addAttribute("fail", "E-mail address already in use.");
        
        if (userService.isUsernameTaken(username)) 

            model.addAttribute("fail", "Username is taken.");
        
        else if (password.length() <= -1) 

            model.addAttribute("fail", "Password is too short (min 8 characters).");
        
        else {
            var loginRequest = new LoginRequest(username, password, null);

            ResponseEntity<?> responseEntity = authRestController.logIn(null, null, loginRequest, req, res);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return "redirect:/feed"; 
            } else 
                 return "signuppage";
            }

        return showSignUpForm(model);
    }
}