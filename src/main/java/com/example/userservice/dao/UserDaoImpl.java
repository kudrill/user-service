package com.example.userservice.dao;

import com.example.userservice.model.User;
import com.example.userservice.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public User create(User user) throws Exception {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            return user;
        } catch (HibernateException he) {
            if (tx != null) tx.rollback();
            log.error("Error creating user", he);
            throw new Exception("Ошибка при создании пользователя: " + he.getMessage(), he);
        }
    }

    @Override
    public Optional<User> findById(Long id) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        } catch (HibernateException he) {
            log.error("Error finding user by id", he);
            throw new Exception("Ошибка при чтении пользователя: " + he.getMessage(), he);
        }
    }

    @Override
    public List<User> findAll() throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from com.example.userservice.model.User", User.class).list();
        } catch (HibernateException he) {
            log.error("Error finding users", he);
            throw new Exception("Ошибка при получении списка пользователей: " + he.getMessage(), he);
        }
    }

    @Override
    public User update(User user) throws Exception {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(user);
            tx.commit();
            return user;
        } catch (HibernateException he) {
            if (tx != null) tx.rollback();
            log.error("Error updating user", he);
            throw new Exception("Ошибка при обновлении пользователя: " + he.getMessage(), he);
        }
    }

    @Override
    public boolean deleteById(Long id) throws Exception {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user == null) {
                tx.rollback();
                return false;
            }
            session.remove(user);
            tx.commit();
            return true;
        } catch (HibernateException he) {
            if (tx != null) tx.rollback();
            log.error("Error deleting user", he);
            throw new Exception("Ошибка при удалении пользователя: " + he.getMessage(), he);
        }
    }
}
