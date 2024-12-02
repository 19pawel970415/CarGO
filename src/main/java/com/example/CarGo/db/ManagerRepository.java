package com.example.CarGo.db;

import com.example.CarGo.domain.Manager;
import com.example.CarGo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
    Optional<Manager> findByLogin(String login);
    Optional<Manager> findByEmail(String email);
}
