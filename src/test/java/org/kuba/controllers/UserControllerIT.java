package org.kuba.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kuba.model.UserDAO;
import org.kuba.model.UserDTO;
import org.kuba.model.account.AccountDeletionResponse;
import org.kuba.security.config.WebSecurityConfig;
import org.kuba.service.jwt.JwtService;
import org.kuba.service.userService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Import(WebSecurityConfig.class)
class UserControllerIT {
  @Autowired private MockMvc mockMvc;
  @MockBean private UserService userService;
  @MockBean private JwtService jwtService;
  @MockBean private AuthenticationManager authenticationManager;

  @Test
  @DisplayName("Register user and expect 200")
  void registerUser_And_Except_Success() throws Exception {
    UserDTO userDTO = new UserDTO("kuba", "mail2@mail.com", "2888288282");
    when(userService.registerUser(any(UserDAO.class))).thenReturn(userDTO);
    this.mockMvc
        .perform(
            post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"username\":\"kuba\",\"email\":\"mail2@mail.com\",\"phoneNumber\":\"2888288282\",\"password\":\"Pass123@\"}")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.username").value("kuba"))
        .andExpect(jsonPath("$.email").value("mail2@mail.com"))
        .andExpect(jsonPath("$.phoneNumber").value("2888288282"));
  }

  UserDetails createUser() {
    return new User("user", "pass123", true, true, true, true, Collections.emptyList());
  }

  @Test
  @Rollback(value = false)
  @DisplayName("test if we can delete account with expired token and get 401")
  void deleteAccountWithToken_and_get_401() throws Exception {
    String token =
        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJLdWJhIEEiLCJpYXQiOjE2ODgwNDYyODgsImV4cCI6MTY4ODEzMjY4OH0.d7DNJbsIrN_FxF694ucSNLdYNyi90IMbBtINUVBiY-k57uNoL6Y_vMl92O0N7xHbEGyMOWdMDE0XKPmkH3F2xg";
    Date date = new Date(System.currentTimeMillis());
    when(userService.deleteUserAccount(token))
        .thenReturn(new AccountDeletionResponse("mail@mail.com", date));

    this.mockMvc
        .perform(
            post("/user/settings/delete-account")
                .contentType("application/json")
                .content(
                    "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJLdWJhIEEiLCJpYXQiOjE2ODgwNDYyODgsImV4cCI6MTY4ODEzMjY4OH0.d7DNJbsIrN_FxF694ucSNLdYNyi90IMbBtINUVBiY-k57uNoL6Y_vMl92O0N7xHbEGyMOWdMDE0XKPmkH3F2xg"))
        .andExpect(status().is4xxClientError());
  }

  @Test
  @DisplayName("Test if internal filter works")
  void test_if_we_can_access_not_exisiting_endpoint_and_get_401() throws Exception {
    this.mockMvc
        .perform(
            post("/user/settingsss/delete-accountaa")
                .contentType("application/json")
                .content("test"))
        .andExpect(status().is4xxClientError());
  }
}
