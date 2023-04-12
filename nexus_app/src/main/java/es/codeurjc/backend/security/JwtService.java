package es.codeurjc.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService
{
    // NOTE not production ready, the secret key should be an env variable
    private static final String SECRET_KEY = "4B6250655368566D5970337336763979244226452948404D635166546A576E5A";
    private static final int TOKEN_EXPIRATION_MS = 1000 * 60 * 24;

    public static boolean userAlreadyAuthenticated()
    {
        return Option.of(
            SecurityContextHolder.getContext().getAuthentication()
        ).isDefined();
    }

    public static String generateToken(UserDetails userDetails)
    {
        return generateToken(Map.of(), userDetails);
    }

    /**
     * @param  extraClaims extra data to add to the JWT not in `userDetails`
     * @return new JWT for `userDetails`
     */
    public static String generateToken(
        Map<String, Object> extraClaims, UserDetails userDetails
    ) {
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MS))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public static boolean isTokenValid(Optional<String> jwt, Optional<UserDetails> ud)
    {
        if (jwt.isEmpty() || ud.isEmpty()) return false;
        return isTokenValid(jwt.get(), ud.get());
    }

    public static boolean isTokenValid(String jwt, UserDetails userDetails)
    {
        return (
            extractUsername(jwt).equals(userDetails.getUsername()) &&
            !isTokenExpired(jwt)
        );
    }

    private static boolean isTokenExpired(String jwt)
    {
        return extractClaim(jwt, Claims::getExpiration).before(new Date());
    }

    /**
     * @return the username, codified in the JWT subject
     */
    public static String extractUsername(String jwt)
    {
        return extractClaim(jwt, Claims::getSubject);
    }

    public static <T> T extractClaim(String jwt, Function<Claims, T> f)
    {
        final Claims claims = extractAllClaims(jwt);
        return f.apply(claims);
    }

    private static Claims extractAllClaims(String jwt)
    {
        return Jwts
            .parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(jwt)
            .getBody();
    }

    private static Key getSignInKey()
    {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
