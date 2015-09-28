package org.ado.biblio.db;

/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Andoni del Olmo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import io.dropwizard.hibernate.AbstractDAO;
import org.ado.biblio.model.Book;
import org.ado.biblio.model.User;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * @author Andoni del Olmo
 * @since 19.09.15
 */
public class BookDao extends AbstractDAO<Book> {
    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory a session provider
     */
    public BookDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Book save(Book book) {
        return persist(book);
    }

    public Book findById(User user, Long id) {
        return uniqueResult(namedQuery("org.ado.biblio.model.Book.findById")
                .setLong("userId", user.getId())
                .setLong("id", id));
    }

    public Book findByIsbn(User user, String isbn) {
        return uniqueResult(namedQuery("org.ado.biblio.model.Book.findByIsbn")
                .setLong("userId", user.getId())
                .setString("isbn", isbn));
    }

    public List<Book> findAll(User user) {
        return list(namedQuery("org.ado.biblio.model.Book.findAll")
                .setLong("userId", user.getId()));
    }

    public void delete(Book book) {
        currentSession().delete(book);
    }
}
