package com.example.userservice.dao;

import com.example.userservice.model.User;
import com.example.userservice.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDaoIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");

    private static SessionFactory sessionFactory;
    private static UserDao userDao;

    @BeforeAll
    static void setup() {
        postgres.start();


        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());

        sessionFactory = HibernateUtil.getSessionFactory();
        userDao = new UserDaoImpl();
    }

    @BeforeEach
    void cleanDatabase() {
        // Очищаем таблицу users перед каждым тестом
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("delete from User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @AfterAll
    static void tearDown() {
        postgres.stop();
    }

    @Test
    @Order(1)
    void testCreateUser() throws Exception {
        User user = new User("Alice", "alice@test.com", 25);
        User created = userDao.create(user);

        assertNotNull(created.getId());
        assertEquals("Alice", created.getName());
    }

    @Test
    @Order(2)
    void testFindById() throws Exception {
        User user = new User("Bob", "bob@test.com", 30);
        userDao.create(user);

        Optional<User> found = userDao.findById(user.getId());
        assertTrue(found.isPresent());
        assertEquals("Bob", found.get().getName());
    }

    @Test
    @Order(3)
    void testFindAll() throws Exception {
        userDao.create(new User("Eve", "eve@test.com", 22));
        userDao.create(new User("Frank", "frank@test.com", 29));

        List<User> users = userDao.findAll();
        assertEquals(2, users.size());
    }

    @Test
    @Order(4)
    void testUpdateUser() throws Exception {
        User user = new User("Charlie", "charlie@test.com", 40);
        userDao.create(user);

        user.setAge(41);
        User updated = userDao.update(user);

        assertEquals(41, updated.getAge());
    }

    @Test
    @Order(5)
    void testDeleteUser() throws Exception {
        User user = new User("David", "david@test.com", 50);
        userDao.create(user);

        boolean deleted = userDao.deleteById(user.getId());
        assertTrue(deleted);
    }
}
