package org.kuba.controllers;

import jakarta.transaction.Transactional;
import org.kuba.exception.UserDataIsNotValidException;
import org.kuba.exception.WeakPasswordException;
import org.kuba.model.UserDAO;
import org.kuba.model.account.ChangePasswordRequest;
import org.kuba.model.account.PasswordChangeResponse;
import org.kuba.model.login.LoginRequest;
import org.kuba.model.login.LoginResponse;
import org.kuba.service.jwt.JwtService;
import org.kuba.service.userService.UserService;
import org.kuba.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user/")
@RestController
@Validated
@CrossOrigin
public class UserController {
  private final JsonUtil jsonUtil = new JsonUtil();
  @Autowired private UserService userService;
  @Autowired private UserDetailsService userDetailsService;
  @Autowired private JwtService jwtService;
  @Autowired private AuthenticationManager authenticationManager;

  @PostMapping("register")
  public ResponseEntity<String> registerUser(@RequestBody UserDAO userDAO)
      throws UserDataIsNotValidException {
    var userDTO = userService.registerUser(userDAO);
    return ResponseEntity.status(200).body(jsonUtil.toJson(userDTO));
  }

  @PostMapping("login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
      var response = userService.login(request);
      return ResponseEntity.status(200)
              .header(HttpHeaders.SET_COOKIE, response.jwtCookie().toString())
              .body(response);

  }

  @PostMapping("logout")
  public ResponseEntity<String> logout() {
    ResponseCookie cookie =
        jwtService.getCleanJwtCookie();
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body("Signed out...");
  }

  @DeleteMapping("settings/delete-account")
  @Transactional
  public ResponseEntity<String> deleteUserAccount(@RequestBody String token) {
    var user = userService.deleteUserAccount(token);
    return ResponseEntity.ok()
        .body("Deleted user account with username: " + user.email() + " at: " + user.timestamp());
  }

  @PostMapping("/change-password")
  public ResponseEntity<?> changePassword(
          @RequestBody ChangePasswordRequest changePasswordRequest) {
    try {
      userService.changePassword(changePasswordRequest);
      return ResponseEntity.ok(new PasswordChangeResponse("Password changed successfully"));
    } catch (WeakPasswordException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Internal Server Error");
    }
  }
}
