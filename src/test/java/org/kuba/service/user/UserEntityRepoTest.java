package org.kuba.service.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kuba.model.UserDAO;
import org.kuba.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserEntityRepoTest {
  @Autowired private TestEntityManager testEntityManager;
  @Autowired private UserRepository userRepository;

  @Test
  @DisplayName("Check if components are not null")
  void providedComponentsAreNotNull() {
    assertThat(testEntityManager).isNotNull();
    assertThat(userRepository).isNotNull();
  }

  @Test
  @DisplayName("Check if db is empty")
  void should_find_noUsersIfRepoIsEmpty() {
    Iterable<UserDAO> users = userRepository.findAll();
    assertThat(users).isEmpty();
  }

  @Test
  @DisplayName("Store user in test db")
  void should_store_user() {
    UserDAO user =
        userRepository.save(new UserDAO(1L, "user", "user@mail.com", "800800800", "Pass123@"));
    assertThat(user).isNotNull();
    assertThat(user).hasFieldOrPropertyWithValue("username", "user");
    assertThat(user).hasFieldOrPropertyWithValue("email", "user@mail.com");
    assertThat(user).hasFieldOrPropertyWithValue("phoneNumber", "800800800");
    assertThat(user).hasFieldOrPropertyWithValue("password", "Pass123@");
  }

  @Test
  @DisplayName("Create 3 users and test if they exist")
  void should_find_all_users() {
    createUsers();
    Iterable<UserDAO> users = userRepository.findAll();
    assertThat(users).hasSize(3).containsAll(users);
  }

  void createUsers() {
    UserDAO user1 =
        userRepository.save(new UserDAO(1L, "user1", "user1@mail.com", "800800800", "Pass123@"));
    UserDAO user2 =
        userRepository.save(new UserDAO(2L, "user2", "user2@mail.com", "200800800", "Pass123@"));
    UserDAO user3 =
        userRepository.save(new UserDAO(3L, "user3", "user3@mail.com", "300800800", "Pass123@"));
    testEntityManager.persist(user1);
    testEntityManager.persist(user2);
    testEntityManager.persist(user3);
  }

  @Test
  @DisplayName("Should find user by username")
  void should_find_user_by_username() {
    UserDAO user1 =
        userRepository.save(new UserDAO(1L, "user1", "user1@mail.com", "800800800", "Pass123@"));
    UserDAO foundUser =
        userRepository.findByUsername(user1.getUsername()).get(); // to fix later to lambda
    assertThat(foundUser).isEqualTo(user1);
  }

  @Test
  @DisplayName("Should find user by email")
  void should_find_user_by_email() {
    UserDAO user1 =
        userRepository.save(new UserDAO(1L, "user1", "user1@mail.com", "800800800", "Pass123@"));
    UserDAO foundUser =
        userRepository.findByEmail(user1.getEmail()).get(); // to fix later to lambda
    assertThat(foundUser).isEqualTo(user1);
  }

  @Test
  @DisplayName("Should find user by phoneNumber")
  void should_find_user_by_phoneNumber() {
    UserDAO user1 =
        userRepository.save(new UserDAO(1L, "user1", "user1@mail.com", "800800800", "Pass123@"));
    UserDAO foundUser =
        userRepository.findByPhoneNumber(user1.getPhoneNumber()).get(); // to fix later to lambda
    assertThat(foundUser).isEqualTo(user1);
  }

  @Test
  @DisplayName("Should delete user by email")
  void should_delete_user_by_email() {
    UserDAO user1 =
        userRepository.save(new UserDAO(1L, "user1", "user1@mail.com", "800800800", "Pass123@"));
    userRepository.deleteByEmail(user1.getEmail());
    assertThat(userRepository.findByEmail("user1@mail.com")).isEmpty();
  }

  @Test
  @DisplayName("Should delete all users")
  void delete_all_users_from_db() {
    createUsers();
    userRepository.deleteAll();
    assertThat(userRepository.findAll()).isEmpty();
  }
}
