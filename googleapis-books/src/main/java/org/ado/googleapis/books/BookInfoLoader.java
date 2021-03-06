package org.ado.googleapis.books;

import com.google.gson.Gson;
import org.ado.googleapis.books.json.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;

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
 * Class description here.
 *
 * @author andoni
 * @since 25.10.2014
 */
public class BookInfoLoader {

    private final Logger LOGGER = LoggerFactory.getLogger(BookInfoLoader.class);
    private static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?q=%s";

    public BookInfo getBookInfo(String isbn) throws IOException, NoBookInfoFoundException {
        String url = String.format(GOOGLE_BOOKS_URL, isbn);
        LOGGER.info("Book search url [{}]", url);

        Volumes volumes = new Gson().fromJson(IOUtils.toString(new URL(url).openStream()), Volumes.class);
        if (volumes == null || volumes.getTotalItems() == 0 || volumes.getItems() == null) {
            throw new NoBookInfoFoundException(isbn);
        }
        return getBookInfo(volumes.getItems().get(0));
    }

    private BookInfo getBookInfo(Item item) {
        BookInfo bookInfo = new BookInfo();
        VolumeInfo volumeInfo = item.getVolumeInfo();

        if (volumeInfo != null) {
            StringBuilder authors = new StringBuilder();
            if (volumeInfo.getAuthors() != null && !volumeInfo.getAuthors().isEmpty()) {
                for (String author : volumeInfo.getAuthors()) {
                    authors.append(author).append(", ");
                }
            }
            bookInfo.setAuthor(authors.substring(0, authors.lastIndexOf(",")));

            bookInfo.setTitle(volumeInfo.getTitle());

            ImageLinks imageLinks = volumeInfo.getImageLinks();
            if (imageLinks != null) {
                bookInfo.setThumbnailUrl(imageLinks.getThumbnail());
            }

            List<IndustryIdentifier> industryIdentifiers = volumeInfo.getIndustryIdentifiers();
            for (IndustryIdentifier industryIdentifier : industryIdentifiers) {
                if (IndustryIdentifierTypeEnum.ISBN_13 == industryIdentifier.getType()) {
                    bookInfo.setIsbn(industryIdentifier.getIdentifier());
                }
            }
        }

        return bookInfo;
    }
}
