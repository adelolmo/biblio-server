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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Andoni del Olmo
 * @since 18.09.15
 */
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "org.ado.biblio.model.Book.findAll",
                query = "select * from book",
                resultClass = Book.class
        )
})
@Entity
@Table(name = "book")
public class Book implements Serializable {

    @JsonProperty(value = "id")
    @Id
    @Column(name = "id")
    private String _id;

    @JsonProperty(value = "format")
    @NotEmpty
    @Column(name = "format")
    private String _format;

    @JsonProperty(value = "code")
    @NotEmpty
    @Column(name = "code")
    private String _code;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }

    public String getFormat() {
        return _format;
    }

    public void setFormat(String format) {
        _format = format;
    }

    public String getCode() {
        return _code;
    }

    public void setCode(String code) {
        _code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        if (_id != null ? !_id.equals(book._id) : book._id != null) return false;
        if (_format != null ? !_format.equals(book._format) : book._format != null) return false;
        return !(_code != null ? !_code.equals(book._code) : book._code != null);

    }

    @Override
    public int hashCode() {
        int result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + (_format != null ? _format.hashCode() : 0);
        result = 31 * result + (_code != null ? _code.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Book{");
        sb.append("_id='").append(_id).append('\'');
        sb.append(", _format='").append(_format).append('\'');
        sb.append(", _code='").append(_code).append('\'');
        sb.append('}');
        return sb.toString();
    }

}