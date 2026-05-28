package com.amichaimarcus.investment.service;

import com.amichaimarcus.investment.model.User;
import com.amichaimarcus.investment.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // פונקציית רישום משתמש חדש
    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("שם המשתמש כבר תפוס!");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("האימייל כבר קיים במערכת!");
        }

        // הצפנת הסיסמה לפני השמירה במסד הנתונים!
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    // פונקציית התחברות למשתמש קיים
    public User loginUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // בדיקת הסיסמה מול ההצפנה השמורה
            if (BCrypt.checkpw(password, user.getPassword())) {
                return user; // התחברות מוצלחת!
            } else {
                throw new RuntimeException("סיסמה שגויה");
            }
        } else {
            throw new RuntimeException("משתמש לא נמצא במערכת");
        }
    }
}