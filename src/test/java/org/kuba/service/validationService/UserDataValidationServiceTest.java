package org.kuba.service.validationService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.kuba.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

class UserDataValidationServiceTest {
  @Autowired private UserRepository userRepository;

  @Autowired
  UserDataValidationService validationService = new UserDataValidationService(userRepository);

  @Test
  void testIfPasswordIsCorrect() {
    assertTrue(validationService.isPasswordCorrect("Password123@"));
  }

  @Test
  void testIfPasswordIsNotCorrect() {
    assertFalse(validationService.isPasswordCorrect("fad1ks"));
  }

  @Test
  void testIfEmailIsCorrect() {
    assertTrue(validationService.isEmailCorrect("email@mail.com"));
  }

  @Test
  void testIfEmailIsNotCorrect() {
    assertFalse(validationService.isEmailCorrect("mail.mail.com"));
  }

  @Test
  void testIfUsernameIsCorrect() {
    assertTrue(validationService.isUsernameCorrect("username"));
  }

  @Test
  void testIfUsernameIsNotCorrect() {
    assertFalse(validationService.isUsernameCorrect("username@mail.com"));
  }

  @Test
  void testIfPhoneNumberIsCorrect() {
    assertTrue(validationService.isPhoneNumberCorrect("123123123"));
  }

  @Test
  void testIfPhoneNumberIsNotCorrect() {
    assertFalse(validationService.isPhoneNumberCorrect("123123123123a"));
  }
}
