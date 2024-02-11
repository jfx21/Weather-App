package org.kuba.service.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.kuba.model.UserDTO;
import org.kuba.repository.UserRepository;
import org.kuba.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Slf4j
@Component
public class JwtService {
  private final UserRepository userRepository;
  private final JsonUtil jsonUtil = new JsonUtil();

  @Value("${jwt.expiration}")
  private long jwtExpiration;

  private long refreshExpiration;

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.cookie-name}")
  private String jwtCookie;

  public JwtService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return doGenerateToken(claims, userDetails, jwtExpiration);
  }

  private String doGenerateToken(
      Map<String, Object> claims, UserDetails userDetails, long expiration) {
    long milis = System.currentTimeMillis();
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(milis))
        .setExpiration(new Date(milis + expiration))
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();
  }

  public String getJwtFromCookies(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, jwtCookie);
    if (cookie != null) {
      return cookie.getValue();
    } else {
      return null;
    }
  }

  public ResponseCookie generateJwtCookie(UserDetails userDetails) {
    String jwt = generateToken(userDetails);
    return ResponseCookie.from(jwtCookie, jwt)
        .path("/")
        .maxAge(24 * 60 * 60)
            .sameSite("None")
            .secure(true)
        .httpOnly(true)
        .build();
  }

  public ResponseCookie getCleanJwtCookie() {
    return ResponseCookie.from(jwtCookie, null).path("/").build();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(authToken)
          .getBody();
      return true;
    } catch (MalformedJwtException e) {
      log.debug("Invalid JWT token: ", e);
    } catch (ExpiredJwtException e) {
      log.debug("JWT token is expired: ", e);
    } catch (UnsupportedJwtException e) {
      log.debug("JWT token is unsupported: ", e);
    } catch (IllegalArgumentException e) {
      log.debug("JWT claims string is empty: ", e);
    }

    return false;
  }

  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public Optional<String> checkTokenValidity(HttpServletRequest request) {
    var authToken = getJwtFromCookies(request);
    if (validateJwtToken(authToken)) {
      var username = getUsernameFromToken(authToken);
      var user = userRepository.findByUsername(username);
      if (user.isPresent()) {
        var response =
            new UserDTO(
                user.get().getUsername(), user.get().getEmail(), user.get().getPhoneNumber());
        return jsonUtil.toJson(response).describeConstable();
      }
    }
    return Optional.empty();
  }
}
