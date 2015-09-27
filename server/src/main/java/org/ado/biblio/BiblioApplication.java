package org.ado.biblio;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthFactory;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.ado.biblio.auth.TokenAuthFactory;
import org.ado.biblio.auth.UserAuthenticator;
import org.ado.biblio.config.CacheConfiguration;
import org.ado.biblio.db.BookDao;
import org.ado.biblio.db.SessionDao;
import org.ado.biblio.db.UserDao;
import org.ado.biblio.model.Book;
import org.ado.biblio.model.User;
import org.ado.biblio.resources.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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

/**
 * @author Andoni del Olmo
 * @since 16.09.15
 */
public class BiblioApplication extends Application<BiblioConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BiblioApplication.class);

    private final HibernateBundle<BiblioConfiguration> hibernate = new HibernateBundle<BiblioConfiguration>(Book.class, User.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(BiblioConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    public static void main(String[] args) throws Exception {
        new BiblioApplication().run(args);
    }

    @Override
    public String getName() {
        return "Biblio REST Server";
    }

    @Override
    public void initialize(Bootstrap<BiblioConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(new DBIExceptionsBundle());
        bootstrap.addBundle(new DBMigrationsBundle());
    }

    @Override
    public void run(BiblioConfiguration configuration, Environment environment) throws Exception {
//        final DBIFactory dbiFactory = new DBIFactory();
//        final DBI jdbi = dbiFactory.build(environment, configuration.getDataSourceFactory(), "mysql");

        CacheConfiguration cacheConfig = configuration.getCacheConfiguration();
        final JedisPool pool = new JedisPool(cacheConfig.getAddress(), cacheConfig.getPort());

        Jedis jedis = pool.getResource();
        jedis.configSet("timeout", cacheConfig.getSessionExpiration());
        pool.returnResource(jedis);

        final UserDao userDao = new UserDao(hibernate.getSessionFactory());
        final BookDao bookDao = new BookDao(hibernate.getSessionFactory());

        final SessionDao sessionDao = new SessionDao(pool, configuration.getCacheConfiguration());

        environment.jersey().register(new UserResource(userDao, sessionDao));
        environment.jersey().register(new BarCodeResource(bookDao));
        environment.jersey().register(new BookResource(bookDao));
        environment.jersey().register(new IsbnResource(bookDao));
        environment.jersey().register(new LendResource());

//        environment.jersey().register(new AuthorizedProvider(userDao, sessionDao));
        environment.jersey()
                .register(AuthFactory.binder(new TokenAuthFactory<User>(new UserAuthenticator(userDao, sessionDao), "SUPER SECRET STUFF", User.class)));
    }
}