package com.amichaimarcus.investment.repository;

import com.amichaimarcus.investment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // חיפוש לפי שם משתמש
    Optional<User> findByUsername(String username);

    // חיפוש לפי אימייל (השורה שחסרה לך!)
    Optional<User> findByEmail(String email);
}