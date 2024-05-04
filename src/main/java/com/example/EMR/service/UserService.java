package com.example.EMR.service;

import com.example.EMR.models.Patient;
import com.example.EMR.models.User;
import com.example.EMR.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public boolean verifyUser(UUID employeeId) {
        User user = userRepository.findById(employeeId).orElse(null);
        if (user != null) {
            User.EmployeeStatus employeeStatus = user.getEmployeeStatus();
            return employeeStatus == User.EmployeeStatus.DELETED;
        } else {
            // Handle the case when user is not found
            return false;
        }
    }
}
