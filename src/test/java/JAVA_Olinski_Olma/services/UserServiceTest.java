package JAVA_Olinski_Olma.services;

import JAVA_Olinski_Olma.model.Project;
import JAVA_Olinski_Olma.model.Users;
import JAVA_Olinski_Olma.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateUser_verifySaveCalled() {
        Users user = new Users();
        user.setUsername("Janusz");
        when(userRepository.save(any(Users.class))).thenReturn(user);

        Users result = userService.createUser(user);

        assertNotNull(result);
        assertEquals("Janusz", result.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldGetAllUsers() {
        List<Users> users = new ArrayList<>();
        Users u1 = new Users(); u1.setUsername("A");
        Users u2 = new Users(); u2.setUsername("B");
        users.add(u1); users.add(u2);

        when(userRepository.findAll()).thenReturn(users);

        List<Users> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void shouldGetUserById_present() {
        Users user = new Users();
        user.setUsername("Kowalski");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<Users> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("Kowalski", result.get().getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenGetUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Users> result = userService.getUserById(1L);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void shouldUpdateUserWhenUserExistsAndHasProjects() {
        Users existingUser = new Users();
        existingUser.setUsername("stary_username");

        Users userDetails = new Users();
        userDetails.setUsername("nowy_username");
        userDetails.setProjects(new HashSet<>()); // non-null projects

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Users> result = userService.updateUser(1L, userDetails);

        assertTrue(result.isPresent());
        assertEquals("nowy_username", result.get().getUsername());
        assertNotNull(result.get().getProjects());
        verify(userRepository, times(1)).save(existingUser);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void shouldUpdateUserWhenProjectsNull_preserveExistingProjects() {
        Users existingUser = new Users();
        existingUser.setUsername("stary");
        Set<Project> existingProjects = new HashSet<>();
        Project p = new Project();
        p.setName("P1");
        existingProjects.add(p);
        existingUser.setProjects(existingProjects);

        Users userDetails = new Users();
        userDetails.setUsername("nowy");
        userDetails.setProjects(null); // kluczowy przypadek

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Users> result = userService.updateUser(1L, userDetails);

        assertTrue(result.isPresent());
        assertEquals("nowy", result.get().getUsername());
        assertNotNull(result.get().getProjects());
        assertEquals(1, result.get().getProjects().size(), "Istniejące projekty powinny pozostać niezmienione");
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void shouldNotUpdateUserWhenUserDoesNotExist() {
        Users userDetails = new Users();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Users> result = userService.updateUser(1L, userDetails);

        assertFalse(result.isPresent());
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void shouldDeleteUserWhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldNotDeleteUserWhenUserDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        boolean result = userService.deleteUser(1L);

        assertFalse(result);
        verify(userRepository, never()).deleteById(anyLong());
    }
}