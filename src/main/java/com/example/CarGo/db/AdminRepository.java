package com.example.CarGo.db;

import com.example.CarGo.domain.Admin;
import com.example.CarGo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
    Optional<Admin> findByLogin(String login);
    Optional<Admin> findByEmail(String email);
}
