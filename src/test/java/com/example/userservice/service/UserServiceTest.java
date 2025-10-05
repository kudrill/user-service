package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() throws Exception {
        User user = new User("Alice", "alice@example.com", 25);
        when(userDao.create(any(User.class))).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals("Alice", result.getName());
        verify(userDao, times(1)).create(user);
    }

    @Test
    void testGetUser() throws Exception {
        User user = new User("Bob", "bob@example.com", 30);
        when(userDao.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> found = userService.getUser(1L);
        assertTrue(found.isPresent());
        assertEquals("Bob", found.get().getName());
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userDao.findAll()).thenReturn(List.of(new User("X", "x@x.com", 10)));

        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
        verify(userDao).findAll();
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = new User("Charlie", "c@example.com", 40);
        when(userDao.update(user)).thenReturn(user);

        User updated = userService.updateUser(user);
        assertEquals("Charlie", updated.getName());
        verify(userDao).update(user);
    }

    @Test
    void testDeleteUser() throws Exception {
        when(userDao.deleteById(1L)).thenReturn(true);

        boolean result = userService.deleteUser(1L);
        assertTrue(result);
        verify(userDao).deleteById(1L);
    }
}
