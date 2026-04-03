package JAVA_Olinski_Olma.controller;

import JAVA_Olinski_Olma.model.Users;
import JAVA_Olinski_Olma.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
private final UserRepository userRepository;

public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
}

@GetMapping
public List<Users> getAllUsers() {
    return userRepository.findAll();
}

@PostMapping
public Users createUser(@RequestBody Users user) {
    return userRepository.save(user);
}
}