package org.ado.biblio.db;

import org.ado.biblio.config.CacheConfiguration;
import org.ado.biblio.core.Session;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;
import java.util.Set;

/**
 * The Session Data Access Object contains methods for accessing and modifying
 * sessions stored in Redis.
 *
 */
public class SessionDao {
    
    public static final String SESSION_KEY_PREFIX = "session:";

    private final JedisPool pool;
    private final Integer sessionExpiration;
    
    public SessionDao(JedisPool pool, CacheConfiguration cacheConfig) {
        this.pool = pool;
        this.sessionExpiration = Integer.parseInt(cacheConfig.getSessionExpiration());
    }

    /**
     * Creates a Session and adds it to the Redis data store.
     * In Redis, a Session is represented as a Hash type with hash values including 
     * the application ID and user ID.
     * @param username The email for the User.
     * @return The newly created Session object is returned.
     */
    public Session createSession(String username) {

        // Create a token and build a Session.
        Session session = new Session();
        session.setUsername(username);
        
        // Add key and user hash data to Redis storage
        String key = SESSION_KEY_PREFIX + session.getSession();
        Jedis jedis = pool.getResource();
        jedis.hmset(key, session.paramMap());
        jedis.expire(key, this.sessionExpiration);
        
        // Return the data storage resource
        pool.returnResource(jedis);
        return session;
    }

    /**
     * Retrieves a session from the Redis data store and updates 
     * the given session parameter.
     * @param session The Session object to query in the data store and update.
     */
    public Session lookupSession(Session session) {
        String keyLookup = SESSION_KEY_PREFIX + session.getSession();
        Jedis jedis = pool.getResource();
        Set<String> fields = jedis.hkeys(keyLookup);
        if (fields.isEmpty()) {
            formatAndThrow(Status.NOT_FOUND, "The session token supplied does not exist");
        }
        
        String[] valKeys = new String[] {Session.USERNAME_PARAM};
        List<String> values = jedis.hmget(keyLookup, valKeys);
        if (values == null || values.size() != valKeys.length) {
            // Something went wrong, log and throw
            formatAndThrow(Status.NOT_FOUND, "The requested session was found to be invalid");
        }
        
        Session queried = new Session(session.getSession());
        queried.setUsername(values.get(0));
        pool.returnResource(jedis);
        return queried;
    }

    /**
     * Deletes the session from the data store. 
     * @param session The representative session object to remove from the data store.
     * @return Return True if the session was deleted from the data store, otherwise, False.
     */
    public boolean deleteSession(Session session) {
        final String key = SESSION_KEY_PREFIX + session.getSession();
        Jedis jedis = pool.getResource();
        
        // If the number of deleted keys returned is equal to 1 then the 
        // session's entry has been removed. 
        long deletedKeys = jedis.del(key);
        pool.returnResource(jedis);
        return (deletedKeys == 1);
    }

    private void formatAndThrow(Response.Status status, String message) {
        throw new WebApplicationException(Response.status(status).entity("{\"error\":\"" + message + "\"}").build());
    }
}
