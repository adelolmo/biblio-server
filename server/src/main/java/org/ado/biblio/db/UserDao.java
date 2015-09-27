package org.ado.biblio.db;

import io.dropwizard.hibernate.AbstractDAO;
import org.ado.biblio.model.User;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andoni del Olmo
 * @since 26.09.15
 */
public class UserDao extends AbstractDAO<User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDao.class);

    public UserDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public User save(User user) {
        return persist(user);
    }

    public User findById(Long id) {
        return get(id);
    }

    public User findByUsername(String username) {
        return uniqueResult(namedQuery("org.ado.biblio.model.User.findByUsername")
                .setString("username", username));
    }

    public void delete(User user) {
        currentSession().delete(user);
    }
}