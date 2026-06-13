package com.ecomm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ecomm.dto.RegisterRequest;
import com.ecomm.dto.UserResponse;
import com.ecomm.entity.User;
import com.ecomm.exception.BusinessException;
import com.ecomm.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private BCryptPasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new BCryptPasswordEncoder();
    }

    @Test
    void registerUser_Success() {
        RegisterRequest request = new RegisterRequest("Siddhesh", "siddhesh@example.com", "password123");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return User.builder()
                    .id(1L)
                    .name(u.getName())
                    .email(u.getEmail())
                    .password(u.getPassword())
                    .role(u.getRole())
                    .build();
        });

        UserResponse response = userService.registerUser(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Siddhesh", response.name());
        assertEquals("siddhesh@example.com", response.email());
        assertEquals(User.ROLE.CUSTOMER, response.role());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ThrowsExceptionWhenEmailExists() {
        RegisterRequest request = new RegisterRequest("Siddhesh", "siddhesh@example.com", "password123");
        User existingUser = User.builder().id(1L).email("siddhesh@example.com").build();
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));

        assertThrows(BusinessException.class, () -> userService.registerUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticate_Success() {
        String email = "siddhesh@example.com";
        String plainPassword = "password123";
        String hashedPassword = encoder.encode(plainPassword);

        User user = User.builder()
                .id(1L)
                .name("Siddhesh")
                .email(email)
                .password(hashedPassword)
                .role(User.ROLE.CUSTOMER)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<UserResponse> authenticatedUser = userService.authenticate(email, plainPassword);

        assertTrue(authenticatedUser.isPresent());
        assertEquals(1L, authenticatedUser.get().id());
        assertEquals("Siddhesh", authenticatedUser.get().name());
    }

    @Test
    void authenticate_FailureWrongPassword() {
        String email = "siddhesh@example.com";
        String plainPassword = "password123";
        String hashedPassword = encoder.encode(plainPassword);

        User user = User.builder()
                .id(1L)
                .name("Siddhesh")
                .email(email)
                .password(hashedPassword)
                .role(User.ROLE.CUSTOMER)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<UserResponse> authenticatedUser = userService.authenticate(email, "wrongpassword");

        assertFalse(authenticatedUser.isPresent());
    }

    @Test
    void getUsers_ReturnsMappedList() {
        User u1 = User.builder().id(1L).name("User1").email("u1@ex.com").role(User.ROLE.CUSTOMER).build();
        User u2 = User.builder().id(2L).name("User2").email("u2@ex.com").role(User.ROLE.ADMIN).build();
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<UserResponse> users = userService.getUsers();

        assertEquals(2, users.size());
        assertEquals("User1", users.get(0).name());
        assertEquals("User2", users.get(1).name());
    }
}
