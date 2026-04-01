package com.finance.infrastructure.repository;

import com.finance.domain.entity.User;
import com.finance.domain.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    boolean existsByEmail(String email);
}
