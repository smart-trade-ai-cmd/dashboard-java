package com.amichaimarcus.investment.controller;

import com.amichaimarcus.investment.model.User;
import com.amichaimarcus.investment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // חובה כדי שהדפדפן יוכל לתקשר עם השרת
public class UserController {

    @Autowired
    private UserService userService;

    // נקודת קצה להתחברות (Login)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            User user = userService.loginUser(username, password);
            return ResponseEntity.ok(user); // מחזיר 200 OK עם פרטי המשתמש
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // מחזיר שגיאה 400
        }
    }

    // נקודת קצה להרשמה (Register)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User newUser = userService.registerUser(user);
            return ResponseEntity.ok(newUser); // מחזיר 200 OK כשההרשמה עברה בהצלחה
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // מחזיר שגיאה 400 (למשל: אימייל תפוס)
        }
    }
}