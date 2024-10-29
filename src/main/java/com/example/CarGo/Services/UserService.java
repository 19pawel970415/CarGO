package com.example.CarGo.Services;

import com.example.CarGo.DB.UserRepository;
import com.example.CarGo.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public String registerUser(User user) {
        if (userRepository.existsByLogin(user.getLogin())) {
            return "Login already exists";
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already exists";
        }

        userRepository.save(user);
        return "User registered successfully";
    }
}
