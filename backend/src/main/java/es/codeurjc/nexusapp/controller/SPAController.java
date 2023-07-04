package es.codeurjc.nexusapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SPAController {
    @GetMapping({"/new/**/{path:[^\\.]*}", "/new/{path:[^\\.]*}"})
    public String redirect() {
        return "forward:/new/index.html";
    }
}
