package com.example.CarGo.DB;

import com.example.CarGo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
    Optional<User> findByLogin(String login);
    Optional<User> findByEmail(String email);
    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.email = :userEmail")
    void updatePassword(@Param("userEmail") String userEmail, @Param("password") String password);

}


