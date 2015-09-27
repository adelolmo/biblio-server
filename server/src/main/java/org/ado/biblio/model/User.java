package org.ado.biblio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.LAZY;

@NamedNativeQueries({
        @NamedNativeQuery(
                name = "org.ado.biblio.model.User.findByUsername",
                query = "select * from user u where u.username = :username",
                resultClass = User.class
        )
})
@Entity
@Table(name = "user")
@JsonIgnoreProperties({"salt", "bookList"})
public class User implements Serializable {

    @JsonProperty(value = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long _id;

    @JsonProperty("username")
    @NotEmpty
    @Column(name = "username")
    private String _username;

    @JsonProperty("password")
    @NotEmpty
    @Column(name = "password")
    private String _password;

    @JsonProperty("salt")
    @Column(name = "salt")
    private String _salt;

    @JsonProperty("role")
    @Column(name = "role")
    private UserRole _role;

    @JsonProperty(value = "ctime")
    @Column(name = "ctime")
    private Date _ctime;

    @JsonProperty("bookList")
    @OneToMany(fetch = LAZY, cascade = PERSIST)
    private List<Book> _bookList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (_id != null ? !_id.equals(user._id) : user._id != null) return false;
        if (_username != null ? !_username.equals(user._username) : user._username != null) return false;
        if (_password != null ? !_password.equals(user._password) : user._password != null) return false;
        if (_salt != null ? !_salt.equals(user._salt) : user._salt != null) return false;
        if (_role != user._role) return false;
        if (_ctime != null ? !_ctime.equals(user._ctime) : user._ctime != null) return false;
        return !(_bookList != null ? !_bookList.equals(user._bookList) : user._bookList != null);

    }

    @Override
    public int hashCode() {
        int result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + (_username != null ? _username.hashCode() : 0);
        result = 31 * result + (_password != null ? _password.hashCode() : 0);
        result = 31 * result + (_salt != null ? _salt.hashCode() : 0);
        result = 31 * result + (_role != null ? _role.hashCode() : 0);
        result = 31 * result + (_ctime != null ? _ctime.hashCode() : 0);
        result = 31 * result + (_bookList != null ? _bookList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("_id=").append(_id);
        sb.append(", _username='").append(_username).append('\'');
        sb.append(", _password='").append(_password).append('\'');
        sb.append(", _salt='").append(_salt).append('\'');
        sb.append(", _role=").append(_role);
        sb.append(", _ctime=").append(_ctime);
//        sb.append(", _bookList=").append(_bookList);
        sb.append('}');
        return sb.toString();
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String password) {
        this._password = password;
    }

    public String getSalt() {
        return _salt;
    }

    public void setSalt(String salt) {
        this._salt = salt;
    }

    public String getUsername() {
        return _username;
    }

    public void setUsername(String username) {
        _username = username;
    }

    public UserRole getRole() {
        return _role;
    }

    public void setRole(UserRole role) {
        _role = role;
    }

    public List<Book> getBookList() {
        return _bookList;
    }

    public void setBookList(List<Book> bookList) {
        _bookList = bookList;
    }

    public Date getCtime() {
        return _ctime;
    }

    public void setCtime(Date ctime) {
        _ctime = ctime;
    }
}
