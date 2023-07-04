package es.codeurjc.nexusapp.controller.rest;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.nexusapp.model.User;
import es.codeurjc.nexusapp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    public record LoginRequest(String username, String password) {}
    
    private final AuthService authService;
    
    @Operation(summary = "Sign up with user details")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "Sign up successful",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
                )}
        ),
        @ApiResponse(
            responseCode = "400", description = "Bad request")})
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(
        @RequestParam String email,
        @RequestParam String username,
        @RequestParam String password
    ) {
        return authService.register(email, username, password);
    }

    @Operation(summary = "Authenticate current session with username and password")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "Authenticated"
        ),
        @ApiResponse(
            responseCode = "400", description = "Wrong password"),
        @ApiResponse(
            responseCode = "404", description = "Couldn't find user")
        })
    @PostMapping("/login")
    public ResponseEntity<?> logIn(
        @CookieValue(name = "accessToken", required = false) String accessToken,
		@CookieValue(name = "refreshToken", required = false) String refreshToken,
        @RequestBody LoginRequest loginRequest,
        @RequestParam Optional<String> username,
        @RequestParam Optional<String> password
    ) {
        if (username.isPresent() && password.isPresent())
            loginRequest = new LoginRequest(username.get(), password.get());

        return authService.login(loginRequest, accessToken, refreshToken);
    }

    @Operation(summary = "Log out of current session")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "Logout successful"
        ),
        @ApiResponse(
            responseCode = "500", description = "Couldn't log out")
        })
    @GetMapping("/logout")
    public ResponseEntity<?> logOut(HttpServletRequest req){
        
        try {
            req.logout();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ServletException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
