package org.kuba.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.kuba.repository.UserRepository;
import org.kuba.service.jwt.JwtService;
import org.kuba.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/")
@CrossOrigin
public class JwtTokenController {
  @Autowired private JwtService jwtService;
  @Autowired private UserRepository userRepository;
  private final JsonUtil jsonUtil = new JsonUtil();

  @PostMapping("jwt-token-check")
  public ResponseEntity<String> isCookieValid(HttpServletRequest request) {
    var response = jwtService.checkTokenValidity(request);
    if (jwtService.checkTokenValidity(request).isPresent())
      return ResponseEntity.ok().body(jsonUtil.toJson(response));
    return ResponseEntity.status(401).body("Provided JWT token is invalid");
  }
}
