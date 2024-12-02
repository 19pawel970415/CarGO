package com.example.CarGo.services;

import com.example.CarGo.db.AdminRepository;
import com.example.CarGo.domain.Admin;
import com.example.CarGo.domain.User;
import jakarta.transaction.Transactional;
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
    public boolean deleteAdminById(Long adminId) {
        try {
            adminRepository.deleteById(adminId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public String updateAdmin(Admin admin) {
        if (adminRepository.existsByEmail(admin.getEmail()) &&
                !adminRepository.findByEmail(admin.getEmail()).get().getId().equals(admin.getId())) {
            return "Email already exists";
        }

        if (adminRepository.existsByLogin(admin.getLogin()) &&
                !adminRepository.findByLogin(admin.getLogin()).get().getId().equals(admin.getId())) {
            return "Login already exists";
        }

        adminRepository.save(admin);
        return "Admin updated successfully";
    }
}
