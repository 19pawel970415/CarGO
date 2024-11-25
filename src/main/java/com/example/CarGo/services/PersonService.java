package com.example.CarGo.services;

import com.example.CarGo.db.PersonRepository;
import com.example.CarGo.domain.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public List<Person> getAllUsers() {
        return personRepository.findAllUsers();
    }

    public List<Person> getAllManagers() {
        return personRepository.findAllManagers();
    }

    public List<Person> getAllAdmins() {
        return personRepository.findAllAdmins();
    }
}
