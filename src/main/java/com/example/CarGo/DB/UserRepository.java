package com.example.CarGo.DB;

import com.example.CarGo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
    Optional<User> findByLogin(String login);

}


