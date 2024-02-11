package org.kuba.service.validationService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.kuba.model.UserDAO;
import org.kuba.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserDataValidationService {
  private final UserRepository userRepository;
  private String message;
  private final String[] phoneNumberException = {
    "PhoneNumber is taken", "Please enter correct phone number"
  };
  private final String[] emailException = {"Email is taken", "Please enter correct email address"};

  public UserDataValidationService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserDataValidationResult validateUserInputs(UserDAO user) {
    UserDataValidationResult result = new UserDataValidationResult();
    if (!this.isEmailCorrect(user.getEmail())) {
      result.getValidationExceptions().getValidationExceptions().add(emailException[1]);
      result.setUserInputCorrect(false);
    }
    if (!this.isPhoneNumberCorrect(user.getPhoneNumber())) {
      result.getValidationExceptions().getValidationExceptions().add(phoneNumberException[1]);
      result.setUserInputCorrect(false);
    }
    if (!this.isUsernameCorrect(user.getUsername())) {
      result
          .getValidationExceptions()
          .getValidationExceptions()
          .add("Username doesn't match regex");
      result.setUserInputCorrect(false);
    }
    if (!this.isPasswordCorrect(user.getPassword())) {
      result
          .getValidationExceptions()
          .getValidationExceptions()
          .add("Password doesn't match regex");
      result.setUserInputCorrect(false);
    }
    if (!this.isEmailTaken(user.getEmail())) {
      result.getValidationExceptions().getValidationExceptions().add(emailException[0]);
      result.setUserInputCorrect(false);
    }
    if (!this.isPhoneNumberTaken(user.getPhoneNumber())) {
      result.getValidationExceptions().getValidationExceptions().add(phoneNumberException[0]);
      result.setUserInputCorrect(false);
    }
    return result;
  }

  public boolean isPasswordCorrect(String password) {
    Pattern p = Pattern.compile("^(?=.*[A-Z])(?=.*[!@#$%^&*()-=_+])[A-Za-z\\d!@#$%^&*()-=_+]{8,}$");
    if (password == null) return false;
    Matcher m = p.matcher(password);
    return m.matches();
  }

  public boolean isEmailCorrect(String email) {
    String emailRegex;
    emailRegex = "[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    Pattern emailPat = Pattern.compile(emailRegex);
    if (email == null) return false;
    return emailPat.matcher(email).matches();
  }

  public boolean isUsernameCorrect(String username) {
    String nameRegex = "^[a-zA-Z0-9_]{3,20}$";
    Pattern namePat = Pattern.compile(nameRegex);
    if (username == null) return false;
    return namePat.matcher(username).matches();
  }

  public boolean isPhoneNumberCorrect(String phoneNumber) {
    String phoneNumberRegex = "^([+]?[\\s0-9]+)?(\\d{3}|[(]?[0-9]+[)])?([-]?[\\s]?[0-9])+$";
    Pattern phonePat = Pattern.compile(phoneNumberRegex);
    if (phoneNumber == null) return false;
    return phonePat.matcher(phoneNumber).matches();
  }

  public boolean isEmailTaken(String email) {
    return userRepository.findByEmail(email).isEmpty();
  }

  public boolean isPhoneNumberTaken(String phoneNumber) {
    return userRepository.findByPhoneNumber(phoneNumber).isEmpty();
  }
}
