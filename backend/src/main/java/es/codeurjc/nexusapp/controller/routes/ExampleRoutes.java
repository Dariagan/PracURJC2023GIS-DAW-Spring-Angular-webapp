package es.codeurjc.nexusapp.controller.routes;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ex")
public class ExampleRoutes
{
    @GetMapping("/admin-str")
    public ResponseEntity<String> getAdminStr()
    {
        return ResponseEntity.ok("You accessed to this string as admin");
    }
    
    @GetMapping("/user-str")
    public ResponseEntity<String> getUserStr()
    {
        return ResponseEntity.ok("You accessed to this string as user");
    }
    
    @GetMapping("/public-str")
    public ResponseEntity<String> getPublicStr()
    {
        return ResponseEntity.ok("You accessed to this string as anon");
    }

    @GetMapping("/name")
    public ResponseEntity<String> getName(Authentication auth)
    {
        return ResponseEntity.ok(auth.getName());
    }
}
