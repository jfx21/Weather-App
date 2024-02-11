package org.kuba.repository;

import java.util.Optional;
import org.kuba.model.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("UserRepo")
public interface UserRepository extends JpaRepository<UserDAO, Long> {
  Optional<UserDAO> findByUsername(String username);

  Optional<UserDAO> findByEmail(String email);

  Optional<UserDAO> findByPhoneNumber(String phoneNumber);

  void deleteByEmail(String email);

  void deleteByUsername(String username);
}
