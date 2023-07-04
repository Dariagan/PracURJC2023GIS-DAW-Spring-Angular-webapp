package es.codeurjc.nexusapp.service;

import java.net.URI;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.codeurjc.nexusapp.utilities.LoginRequest;
import es.codeurjc.nexusapp.model.User;
import es.codeurjc.nexusapp.security.JwtCookieManager;
import es.codeurjc.nexusapp.security.JwtTokenProvider;
import es.codeurjc.nexusapp.security.SecurityCypher;
import es.codeurjc.nexusapp.security.Token;
import es.codeurjc.nexusapp.utilities.AuthResponse;
import es.codeurjc.nexusapp.utilities.responseentity.ResponseBuilder;

@Service
public class AuthService {
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private JwtCookieManager cookieUtil;
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserService userService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public ResponseEntity<AuthResponse> login(LoginRequest loginRequest, String encryptedAccessToken, String 
			encryptedRefreshToken) {
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String accessToken = SecurityCypher.decrypt(encryptedAccessToken);
		String refreshToken = SecurityCypher.decrypt(encryptedRefreshToken);
		
		String username = loginRequest.username();
		UserDetails user = userDetailsService.loadUserByUsername(username);

		boolean accessTokenValid, refreshTokenValid;

		accessTokenValid = jwtTokenProvider.validateToken(accessToken);
		refreshTokenValid = jwtTokenProvider.validateToken(refreshToken);
		

		HttpHeaders responseHeaders = new HttpHeaders();
		Token newAccessToken;
		Token newRefreshToken;
		if (!accessTokenValid && !refreshTokenValid) {
			newAccessToken = jwtTokenProvider.generateToken(user);
			newRefreshToken = jwtTokenProvider.generateRefreshToken(user);
			addAccessTokenCookie(responseHeaders, newAccessToken);
			addRefreshTokenCookie(responseHeaders, newRefreshToken);
		}

		if (!accessTokenValid && refreshTokenValid) {
			newAccessToken = jwtTokenProvider.generateToken(user);
			addAccessTokenCookie(responseHeaders, newAccessToken);
		}

		if (accessTokenValid && refreshTokenValid) {
			newAccessToken = jwtTokenProvider.generateToken(user);
			newRefreshToken = jwtTokenProvider.generateRefreshToken(user);
			addAccessTokenCookie(responseHeaders, newAccessToken);
			addRefreshTokenCookie(responseHeaders, newRefreshToken);
		}

		AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.SUCCESS,
				"Auth successful. Tokens are created in cookie.");
		return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
	}

	public ResponseEntity<?> register(
		LoginRequest loginRequest
    ) {
        //if (!emailService.emailIsValid(email))
            //return ResponseBuilder.badReq("Wrong email provided");
        
        if (userService.isUsernameTaken(loginRequest.username()))
            return ResponseBuilder.badReq("Username already in use");
        
        // NOTE not production ready code
        if (loginRequest.password().length() < 1)
            return ResponseBuilder.badReq("Password too short");
        
        var builder = new User.Builder();
		builder.setUsername(loginRequest.username())
			.setEncodedPassword(passwordEncoder.encode(loginRequest.password()))
			.setEmail(loginRequest.email()).build();

        userService.save(builder.build());
        
        return login(loginRequest, null, null);
    }

	public ResponseEntity<AuthResponse> refresh(String encryptedRefreshToken) {
		
		String refreshToken = SecurityCypher.decrypt(encryptedRefreshToken);
		
		Boolean refreshTokenValid = jwtTokenProvider.validateToken(refreshToken);
		
		if (!refreshTokenValid) {
			AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.FAILURE,
					"Invalid refresh token !");
			return ResponseEntity.ok().body(loginResponse);
		}

		String username = jwtTokenProvider.getUsername(refreshToken);
		UserDetails user = userDetailsService.loadUserByUsername(username);
				
		Token newAccessToken = jwtTokenProvider.generateToken(user);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil
				.createAccessTokenCookie(newAccessToken.getTokenValue(), newAccessToken.getDuration()).toString());

		AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.SUCCESS,
				"Auth successful. Tokens are created in cookie.");
		return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
	}

	public String getUserName() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return authentication.getName();
	}

	public String logout(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession(false);
		SecurityContextHolder.clearContext();
		session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				cookie.setMaxAge(0);
				cookie.setValue("");
				cookie.setHttpOnly(true);
				cookie.setPath("/");
				response.addCookie(cookie);
			}
		}

		return "logout successful";
	}

	private void addAccessTokenCookie(HttpHeaders httpHeaders, Token token) {
		httpHeaders.add(HttpHeaders.SET_COOKIE,
				cookieUtil.createAccessTokenCookie(token.getTokenValue(), token.getDuration()).toString());
	}

	private void addRefreshTokenCookie(HttpHeaders httpHeaders, Token token) {
		httpHeaders.add(HttpHeaders.SET_COOKIE,
				cookieUtil.createRefreshTokenCookie(token.getTokenValue(), token.getDuration()).toString());
	}
}
