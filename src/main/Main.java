package com.example.userservice;

import com.example.userservice.dao.UserDao;
import com.example.userservice.dao.UserDaoImpl;
import com.example.userservice.model.User;
import com.example.userservice.util.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final UserDao userDao = new UserDaoImpl();

    public static void main(String[] args) {
        log.info("Starting user-service application");
        Scanner sc = new Scanner(System.in);

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("> ");
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1": createUser(sc); break;
                    case "2": listUsers(); break;
                    case "3": getUser(sc); break;
                    case "4": updateUser(sc); break;
                    case "5": deleteUser(sc); break;
                    case "0": running = false; break;
                    default: System.out.println("Unknown option"); break;
                }
            } catch (Exception e) {
                log.error("Operation failed", e);
                System.out.println("Ошибка: " + e.getMessage());
            }
        }

        sc.close();
        HibernateUtil.shutdown();
        log.info("Application stopped");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("=== User Service ===");
        System.out.println("1) Create user");
        System.out.println("2) List users");
        System.out.println("3) Get user by id");
        System.out.println("4) Update user");
        System.out.println("5) Delete user");
        System.out.println("0) Exit");
    }

    private static void createUser(Scanner sc) throws Exception {
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Age (number): ");
        Integer age = null;
        String ageStr = sc.nextLine().trim();
        if (!ageStr.isEmpty()) {
            try { age = Integer.parseInt(ageStr); } catch (NumberFormatException nfe) { System.out.println("Неверный возраст"); return; }
        }

        User user = new User(name, email, age);
        User created = userDao.create(user);
        System.out.println("Created: " + created);
    }

    private static void listUsers() throws Exception {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("No users found");
        } else {
            users.forEach(u -> System.out.println(u));
        }
    }

    private static void getUser(Scanner sc) throws Exception {
        System.out.print("Enter id: ");
        Long id = readId(sc);
        if (id == null) return;
        Optional<User> u = userDao.findById(id);
        System.out.println(u.map(Object::toString).orElse("User not found"));
    }

    private static void updateUser(Scanner sc) throws Exception {
        System.out.print("Enter id to update: ");
        Long id = readId(sc);
        if (id == null) return;

        Optional<User> maybe = userDao.findById(id);
        if (maybe.isEmpty()) {
            System.out.println("User not found");
            return;
        }
        User user = maybe.get();
        System.out.println("Current: " + user);

        System.out.print("New name (leave blank to keep): ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) user.setName(name);

        System.out.print("New email (leave blank to keep): ");
        String email = sc.nextLine().trim();
        if (!email.isEmpty()) user.setEmail(email);

        System.out.print("New age (leave blank to keep): ");
        String ageStr = sc.nextLine().trim();
        if (!ageStr.isEmpty()) {
            try { user.setAge(Integer.parseInt(ageStr)); } catch (NumberFormatException nfe) { System.out.println("Неверный возраст"); return; }
        }

        User updated = userDao.update(user);
        System.out.println("Updated: " + updated);
    }

    private static void deleteUser(Scanner sc) throws Exception {
        System.out.print("Enter id to delete: ");
        Long id = readId(sc);
        if (id == null) return;

        boolean ok = userDao.deleteById(id);
        System.out.println(ok ? "Deleted" : "User not found");
    }

    private static Long readId(Scanner sc) {
        String s = sc.nextLine().trim();
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            System.out.println("Неверный id");
            return null;
        }
    }
}

