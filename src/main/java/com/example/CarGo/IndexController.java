package com.example.CarGo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String showIndex() {
        return "index";
    }

    @GetMapping("/about")
    public String showAbout() {
        return "about";
    }

    @GetMapping("/client")
    public String showClient() {
        return "client";
    }

    @GetMapping("/contact")
    public String showContact() {
        return "contact";
    }

    @GetMapping("/gallery")
    public String showGallery() {
        return "gallery";
    }

    @GetMapping("/services")
    public String showServices() {
        return "services";
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }
}
