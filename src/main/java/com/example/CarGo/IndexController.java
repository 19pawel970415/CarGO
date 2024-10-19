package com.example.CarGo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/")
    public String sayHello() {
        return "Welcome to CarGo :)";
    }
}