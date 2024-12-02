package com.example.CarGo.services;

import com.example.CarGo.db.ManagerRepository;
import com.example.CarGo.domain.Admin;
import com.example.CarGo.domain.Manager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagerService {

    @Autowired
    private ManagerRepository managerRepository;

    public String registerManager(Manager manager) {
        managerRepository.save(manager);
        return "Manager registered successfully";
    }
    public boolean deleteManagerById(Long managerId) {
        try {
            managerRepository.deleteById(managerId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public String updateManager(Manager manager) {
        if (managerRepository.existsByEmail(manager.getEmail()) &&
                !managerRepository.findByEmail(manager.getEmail()).get().getId().equals(manager.getId())) {
            return "Email already exists";
        }

        if (managerRepository.existsByLogin(manager.getLogin()) &&
                !managerRepository.findByLogin(manager.getLogin()).get().getId().equals(manager.getId())) {
            return "Login already exists";
        }

        managerRepository.save(manager);
        return "Manager updated successfully";
    }
}
