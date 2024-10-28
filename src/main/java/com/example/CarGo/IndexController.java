package com.example.CarGo;

import com.example.CarGo.Services.CarService;
import com.example.CarGo.models.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private CarService carService;

    @GetMapping(value = {"/", "/index"})
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
    public String showGallery(Model model) {
        List<Car> cars = carService.findAllCars();
        model.addAttribute("cars", cars); // Przekazujemy listę samochodów do modelu
        return "gallery"; // Zwracamy nazwę widoku
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
