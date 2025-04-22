package com.example.Project_zahin.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Project_zahin.models.Admin;

public interface AdminRepository extends JpaRepository<Admin, String> {
    Admin findByUsername(String username);
}