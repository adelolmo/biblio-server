package org.ado.biblio.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Andoni del Olmo
 * @since 21.09.15
 */

public class BarCode {

    @JsonProperty(value = "isbn")
    @NotEmpty
    private String _isbn;

    @JsonProperty(value = "format")
    @NotEmpty
    private String _format;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BarCode barCode = (BarCode) o;

        if (_isbn != null ? !_isbn.equals(barCode._isbn) : barCode._isbn != null) return false;
        return !(_format != null ? !_format.equals(barCode._format) : barCode._format != null);

    }

    @Override
    public int hashCode() {
        int result = _isbn != null ? _isbn.hashCode() : 0;
        result = 31 * result + (_format != null ? _format.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BarCode{");
        sb.append("_isbn='").append(_isbn).append('\'');
        sb.append(", _format='").append(_format).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getIsbn() {
        return _isbn;
    }

    public void setIsbn(String isbn) {
        _isbn = isbn;
    }

    public String getFormat() {
        return _format;
    }

    public void setFormat(String format) {
        _format = format;
    }
}