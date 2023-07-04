package es.codeurjc.nexusapp.controller.deprecated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.text.html.HTML;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import es.codeurjc.nexusapp.utilities.LoginRequest;
import es.codeurjc.nexusapp.controller.rest.AuthRestController;
import es.codeurjc.nexusapp.model.User;
import es.codeurjc.nexusapp.service.UserService;

import java.util.Optional;

//Programmed by group 13-A
@Controller
public class LoginController 
{
    @Autowired 
    private UserService userService;

    @Autowired
    private AuthRestController authRestController;
   
    @GetMapping("/login")
    public String login(Model model)
    {
        model.addAttribute("inLogin", true);


        return "loginpage";
    }

    @PostMapping("/login")
    public String loginReq(Model model, @RequestParam String username, @RequestParam String password,
     HttpServletRequest req, HttpServletResponse res)
    {
        var loginRequest = new LoginRequest(username, password, null);

        ResponseEntity<?> responseEntity = authRestController.logIn(null, null, loginRequest, req, res);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return "redirect:/feed"; 
        } else 
          return "loginpage";
    }

    @RequestMapping("/loginfail")
    public String loginerror(Model model)
    {
        model.addAttribute("fail", "Failed login");
        return "loginpage";
    }

    @RequestMapping("/loginsuccess")
    public String login(HttpServletRequest req, HttpSession session)
    {
        Optional<User> loggedUser = userService.getUserBy(req);
        if (loggedUser.isEmpty()) return "redirect:/loginfail";
        return "redirect:/feed";
    }
}
