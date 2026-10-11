package hotel_ordering.controller;

import hotel_ordering.entity.User;
import hotel_ordering.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174",
    "http://localhost:5175"
})
public class LoginController {

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> credentials) {

        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            return ResponseEntity
                .badRequest()
                .body(Map.of(
                    "message",
                    "Email and password are required."
                ));
        }

        User user = userRepository.findByEmail(email)
            .orElse(null);

        if (user == null || !password.equals(user.getPassword())) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                    "message",
                    "Invalid email or password."
                ));
        }

        if (!"STAFF".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                    "message",
                    "Only staff members can access this dashboard."
                ));
        }

        return ResponseEntity.ok(
            Map.of(
                "message", "Login successful.",
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
            )
        );
    }
}