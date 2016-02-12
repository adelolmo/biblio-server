package org.ado.biblio.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Andoni del Olmo
 * @since 12.10.15
 */
@Entity
@Table(name = "lend")
public class Lend implements Serializable{

    @JsonProperty(value = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long _id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookId", nullable = false)
    private Book _book;

    @JsonProperty(value = "person")
    @NotEmpty
    @Column(name = "person")
    private String _person;

    @JsonProperty(value = "ctime")
    @Column(name = "ctime")
    private Date _ctime;

    @JsonProperty(value = "rtime")
    @Column(name = "rtime")
    private Date _rtime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lend lend = (Lend) o;

        if (_id != null ? !_id.equals(lend._id) : lend._id != null) return false;
        if (_book != null ? !_book.equals(lend._book) : lend._book != null) return false;
        if (_person != null ? !_person.equals(lend._person) : lend._person != null) return false;
        if (_ctime != null ? !_ctime.equals(lend._ctime) : lend._ctime != null) return false;
        return !(_rtime != null ? !_rtime.equals(lend._rtime) : lend._rtime != null);

    }

    @Override
    public int hashCode() {
        int result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + (_book != null ? _book.hashCode() : 0);
        result = 31 * result + (_person != null ? _person.hashCode() : 0);
        result = 31 * result + (_ctime != null ? _ctime.hashCode() : 0);
        result = 31 * result + (_rtime != null ? _rtime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Lend{");
        sb.append("_id=").append(_id);
        sb.append(", _book=").append(_book);
        sb.append(", _person='").append(_person).append('\'');
        sb.append(", _ctime=").append(_ctime);
        sb.append(", _rtime=").append(_rtime);
        sb.append('}');
        return sb.toString();
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public Book getBook() {
        return _book;
    }

    public void setBook(Book book) {
        _book = book;
    }

    public String getPerson() {
        return _person;
    }

    public void setPerson(String person) {
        _person = person;
    }

    public Date getCtime() {
        return _ctime;
    }

    public void setCtime(Date ctime) {
        _ctime = ctime;
    }

    public Date getRtime() {
        return _rtime;
    }

    public void setRtime(Date rtime) {
        _rtime = rtime;
    }
}