package org.kuba.service.userService;

import jakarta.transaction.Transactional;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.kuba.exception.InvalidPasswordException;
import org.kuba.exception.UserDataIsNotValidException;
import org.kuba.exception.UserNotFoundException;
import org.kuba.exception.WeakPasswordException;
import org.kuba.model.UserDAO;
import org.kuba.model.UserDTO;
import org.kuba.model.account.AccountDeletionResponse;
import org.kuba.model.account.ChangePasswordRequest;
import org.kuba.model.login.LoginRequest;
import org.kuba.model.login.LoginResponse;
import org.kuba.repository.UserRepository;
import org.kuba.service.jwt.JwtService;
import org.kuba.service.validationService.UserDataValidationResult;
import org.kuba.service.validationService.UserDataValidationService;
import org.kuba.utils.JsonUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("UserService")
@RequiredArgsConstructor
public class UserService {

  private final JsonUtil jsonUtil = new JsonUtil();
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final UserDataValidationService userDataValidationService;
  private final JwtService jwtService;

  public UserDTO registerUser(UserDAO userDAO) throws UserDataIsNotValidException {

    UserDTO userDTO =
        new UserDTO(userDAO.getUsername(), userDAO.getEmail(), userDAO.getPhoneNumber());
    UserDataValidationResult result = userDataValidationService.validateUserInputs(userDAO);
    if (result.isUserInputCorrect()) {
      userDAO.setPassword(passwordEncoder.encode(userDAO.getPassword()));
      userRepository.save(userDAO);
    } else {
      throw new UserDataIsNotValidException(jsonUtil.toJson(result));
    }
    return userDTO;
  }

  public LoginResponse login(LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password()));
    var user = userRepository.findByEmail(request.email()).orElseThrow();
    var jwtCookie =
        jwtService.generateJwtCookie(user);
    return new LoginResponse(user.getEmail(), user.getUsername(),jwtCookie);
  }

  @Transactional
  public AccountDeletionResponse deleteUserAccount(String token) {
    String username = null;
    Date timestamp = new Date(System.currentTimeMillis());
    if (jwtService.validateJwtToken(token)) {
      username = jwtService.getUsernameFromToken(token);
      var user = userRepository.findByUsername(username);
      user.ifPresent(userDAO -> userRepository.deleteByEmail(userDAO.getEmail()));
    }
    return new AccountDeletionResponse(username, timestamp);
  }
  @Transactional
  public void changePassword(ChangePasswordRequest changePasswordRequest) {
    UserDAO user =
        userRepository
            .findByUsername(changePasswordRequest.username())
            .orElseThrow(() -> new UserNotFoundException("User not found"));

    if (!passwordEncoder.matches(changePasswordRequest.currentPassword(), user.getPassword())) {
      throw new InvalidPasswordException("Current password is incorrect");
    }
    if (!isPasswordStrong(changePasswordRequest.newPassword())) {
      throw new WeakPasswordException("Password strength requirements not met");
    }
    user.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
    userRepository.save(user);
  }

  private boolean isPasswordStrong(String password) {
    return password.length() >= 8
        && password.matches("(?=.*[A-Z])(?=.*[!@#$%^&*()-=_+])[A-Za-z\\d!@#$%^&*()-=_+]{8,}$");
  }
  @Transactional
  public boolean checkUser(String token){
    String username = null;
    if (jwtService.validateJwtToken(token)) {
      username = jwtService.getUsernameFromToken(token);
      return userRepository.findByUsername(username).isPresent();
    }
    return false;
  }
}
