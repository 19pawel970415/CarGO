package com.example.CarGo.DB;

import com.example.CarGo.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    @Query("SELECT p FROM Person p " +
            "LEFT JOIN User u ON p.id = u.id " +
            "LEFT JOIN Manager m ON p.id = m.id " +
            "LEFT JOIN Admin a ON p.id = a.id " +
            "WHERE u.login = :login OR m.login = :login OR a.login = :login")
    Optional<Person> findByLogin(@Param("login") String login);
}
