package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.model.User;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(User user) throws Exception {
        return userDao.create(user);
    }

    public Optional<User> getUser(Long id) throws Exception {
        return userDao.findById(id);
    }

    public List<User> getAllUsers() throws Exception {
        return userDao.findAll();
    }

    public User updateUser(User user) throws Exception {
        return userDao.update(user);
    }

    public boolean deleteUser(Long id) throws Exception {
        return userDao.deleteById(id);
    }
}
