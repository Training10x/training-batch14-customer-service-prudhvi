package com.customer.spring.controller;

import com.customer.spring.entity.User;
import com.customer.spring.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        User user = new User();
        user.setId(Math.toIntExact(1L));
        user.setUsername("john_doe");

        when(userService.register(any(User.class))).thenReturn(user);

        ResponseEntity<Map<String, Object>> response = userController.register(user);

        assertNotNull(response);
        assertTrue(response.getBody().containsKey("message"));
        assertEquals("Registered Successfully", response.getBody().get("message"));
        assertEquals(Math.toIntExact(1L), response.getBody().get("id"));
        assertEquals("john_doe", response.getBody().get("username"));

        verify(userService, times(1)).register(any(User.class));
    }

    @Test
    void testLogin() {
        User user = new User();
        user.setUsername("john_doe");

        when(userService.verify(any(User.class))).thenReturn("mockToken");

        ResponseEntity<Map<String, Object>> response = userController.login(user);

        assertNotNull(response);
        assertTrue(response.getBody().containsKey("message"));
        assertEquals("Login successful", response.getBody().get("message"));
        assertEquals("mockToken", response.getBody().get("token"));
        assertEquals("john_doe", response.getBody().get("user"));

        verify(userService, times(1)).verify(any(User.class));
    }
}
