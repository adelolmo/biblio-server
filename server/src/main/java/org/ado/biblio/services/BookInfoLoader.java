package org.ado.biblio.services;

import org.ado.googleapis.books.AbstractBookInfoLoader;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andoni del Olmo
 * @since 21.09.15
 */
public class BookInfoLoader extends AbstractBookInfoLoader{

    private static final Logger LOGGER = LoggerFactory.getLogger(BookInfoLoader.class);

    @Override
    public HttpClient getHttpClient() {
        return HttpClientBuilder.create().build();
    }

}