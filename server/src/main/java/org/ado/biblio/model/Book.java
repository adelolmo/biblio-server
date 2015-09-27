package org.ado.biblio.model;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Andoni del Olmo
 * @since 18.09.15
 */
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "org.ado.biblio.model.Book.findAll",
                query = "select * from book",
                resultClass = Book.class
        ),
        @NamedNativeQuery(
                name = "org.ado.biblio.model.Book.findByIsbn",
                query = "select * from book b where b.isbn = :isbn",
                resultClass = Book.class
        )
})
@Entity
@Table(name = "book")
public class Book implements Serializable {

    @JsonProperty(value = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long _id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User _user;

    @JsonProperty(value = "title")
    @NotEmpty
    @Column(name = "title")
    private String _title;

    @JsonProperty(value = "author")
    @NotEmpty
    @Column(name = "author")
    private String _author;

    @JsonProperty(value = "ctime")
    @Column(name = "ctime")
    private Date _ctime;

    @JsonProperty(value = "isbn")
    @Column(name = "isbn")
    private String _isbn;

    @JsonProperty(value = "tags")
    @Column(name = "tags")
    private String _tags;

    @JsonProperty(value = "imageUrl")
    @Column(name = "imageUrl")
    private String _imageUrl;
//    private User _user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        if (_id != null ? !_id.equals(book._id) : book._id != null) return false;
        if (_title != null ? !_title.equals(book._title) : book._title != null) return false;
        if (_author != null ? !_author.equals(book._author) : book._author != null) return false;
        if (_ctime != null ? !_ctime.equals(book._ctime) : book._ctime != null) return false;
        if (_isbn != null ? !_isbn.equals(book._isbn) : book._isbn != null) return false;
        if (_tags != null ? !_tags.equals(book._tags) : book._tags != null) return false;
        return !(_imageUrl != null ? !_imageUrl.equals(book._imageUrl) : book._imageUrl != null);

    }

    @Override
    public int hashCode() {
        int result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + (_title != null ? _title.hashCode() : 0);
        result = 31 * result + (_author != null ? _author.hashCode() : 0);
        result = 31 * result + (_ctime != null ? _ctime.hashCode() : 0);
        result = 31 * result + (_isbn != null ? _isbn.hashCode() : 0);
        result = 31 * result + (_tags != null ? _tags.hashCode() : 0);
        result = 31 * result + (_imageUrl != null ? _imageUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Book{");
        sb.append("_id=").append(_id);
        sb.append(", _title='").append(_title).append('\'');
        sb.append(", _author='").append(_author).append('\'');
        sb.append(", _ctime=").append(_ctime);
        sb.append(", _isbn='").append(_isbn).append('\'');
        sb.append(", _tags='").append(_tags).append('\'');
        sb.append(", _imageUrl='").append(_imageUrl).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public void setUser(User user) {
        _user = user;
    }

    public User getUser() {
        return _user;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }

    public String getAuthor() {
        return _author;
    }

    public void setAuthor(String author) {
        _author = author;
    }

    public Date getCtime() {
        return _ctime;
    }

    public void setCtime(Date ctime) {
        _ctime = ctime;
    }

    public String getIsbn() {
        return _isbn;
    }

    public void setIsbn(String isbn) {
        _isbn = isbn;
    }

    public String getTags() {
        return _tags;
    }

    public void setTags(String tags) {
        _tags = tags;
    }

    public String getImageUrl() {
        return _imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        _imageUrl = imageUrl;
    }
}