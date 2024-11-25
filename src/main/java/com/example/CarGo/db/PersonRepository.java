package com.example.CarGo.db;

import com.example.CarGo.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    @Query("SELECT p FROM Person p " +
            "LEFT JOIN User u ON p.id = u.id " +
            "LEFT JOIN Manager m ON p.id = m.id " +
            "LEFT JOIN Admin a ON p.id = a.id " +
            "WHERE u.login = :login OR m.login = :login OR a.login = :login")
    Optional<Person> findByLogin(@Param("login") String login);

    @Query("SELECT p FROM Person p JOIN User u ON p.id = u.id")
    List<Person> findAllUsers();

    @Query("SELECT p FROM Person p JOIN Manager m ON p.id = m.id")
    List<Person> findAllManagers();

    @Query("SELECT p FROM Person p JOIN Admin a ON p.id = a.id")
    List<Person> findAllAdmins();
}
