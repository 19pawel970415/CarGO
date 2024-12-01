package com.example.CarGo.services;

import com.example.CarGo.db.AdminRepository;
import com.example.CarGo.domain.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public String registerAdmin(Admin admin) {
        adminRepository.save(admin);
        return "Admin registered successfully";
    }
}
