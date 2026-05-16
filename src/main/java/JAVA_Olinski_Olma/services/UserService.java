package JAVA_Olinski_Olma.services;
import JAVA_Olinski_Olma.model.Users;
import JAVA_Olinski_Olma.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
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

    public Optional<Users> updateUser(Long id, Users userDetails) {
        Optional<Users> user = userRepository.findById(id);
        if (user.isPresent()) {
            Users existingUser = user.get();
            existingUser.setUsername(userDetails.getUsername());
            if (userDetails.getProjects() != null) {
                existingUser.setProjects(userDetails.getProjects());
            }
            return Optional.of(userRepository.save(existingUser));
        }
        return Optional.empty();
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Users> getUserById(Long id) {
        return userRepository.findById(id);
    }

}