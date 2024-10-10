package com.fiap.security_system.repository;

import com.fiap.security_system.model.Employee;
import com.fiap.security_system.model.ROLES;
import com.fiap.security_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findByRole(ROLES role);
}
