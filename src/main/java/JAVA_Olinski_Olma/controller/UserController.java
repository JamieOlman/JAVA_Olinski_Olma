package JAVA_Olinski_Olma.controller;

import JAVA_Olinski_Olma.model.Users;
import JAVA_Olinski_Olma.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "Operacje na użytkownikach")
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Retrieve all users", description = "Fetches a list of all users from the database")
    @GetMapping("/all")
    public List<Users> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details")
    @PostMapping("/create")
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        Users createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing user", description = "Updates the user with the specified ID")
    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(
            @Parameter(name = "id", description = "The unique identifier of the user to update", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody Users userDetails) {
        var updatedUser = userService.updateUser(id, userDetails);
        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a user by ID", description = "Deletes the user with the specified ID from the database")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(name = "id", description = "The unique identifier of the user to delete", required = true, example = "1")
            @PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
