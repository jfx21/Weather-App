package org.kuba.service.jwt;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {
  @Autowired private JwtService jwtService;
  private UserDetails userDetails;
  private final String token =
      "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJLdWJhIEEiLCJpYXQiOjE2ODgwNDYyODgsImV4cCI6MTY4ODEzMjY4OH0.d7DNJbsIrN_FxF694ucSNLdYNyi90IMbBtINUVBiY-k57uNoL6Y_vMl92O0N7xHbEGyMOWdMDE0XKPmkH3F2xg";

  @Test
  void getUsernameFromToken_with_Success() {
    var user = createUser();
    var token = jwtService.generateToken(user);
    assertEquals("user", jwtService.getUsernameFromToken(token));
  }

  @Test
  void getUsernameFromToken_with_Failure() {
    var user = createUser();
    var token = jwtService.generateToken(user);
    assertNotEquals("Kuba ssA", jwtService.getUsernameFromToken(token));
  }

  @Test
  void getExpirationDateFromToken() {
    var user = createUser();
    var token = jwtService.generateToken(user);
    jwtService.getExpirationDateFromToken(token);
  }

  @Test
  void generateToken_withSuccess() {
    var user = createUser();
    assertNotNull(jwtService.generateToken(user));
  }

  UserDetails createUser() {
    return new User("user", "pass123", true, true, true, true, Collections.emptyList());
  }

  @Test
  void generateJwtCookie() {
    var user = createUser();
    assertNotNull(jwtService.generateJwtCookie(user));
  }

  @Test
  @DisplayName("Get clean cookie")
  void getCleanJwtCookie() {
    var cleanCookie = jwtService.getCleanJwtCookie();
    assertEquals("", cleanCookie.getValue());
  }

  @Test
  @DisplayName("Create token and expect success")
  void validateJwtToken_with_Success() {
    var user = createUser();
    var token = jwtService.generateJwtCookie(user);
    assertNotNull(token);
    assertTrue(jwtService.validateJwtToken(token.getValue()));
  }

  @Test
  @DisplayName("Empty token")
  void validateJwtToken_with_Failure_emptyString() {
    assertFalse(jwtService.validateJwtToken(""));
  }

  @Test
  @DisplayName("Expired token")
  void validateJwtToken_with_Failure_Expired_token() {
    try {
      jwtService.validateJwtToken(token);
      fail("Expected exception...");
    } catch (Exception ex) {
      assertNotNull(ex);
    }
  }

  @Test
  @DisplayName("Malformed JWT Token")
  void validateJwtToken_with_Failure_Invalid_Jwt_Token() {
    assertFalse(jwtService.validateJwtToken("kjskfjalkdjflakjdslfkjalksdjflkajdlkjfslkjfkls"));
  }
}
