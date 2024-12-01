package com.example.CarGo.services;

import com.example.CarGo.db.ManagerRepository;
import com.example.CarGo.domain.Manager;
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
}
