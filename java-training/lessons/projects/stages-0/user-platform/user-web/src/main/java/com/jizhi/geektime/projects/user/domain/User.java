package com.jizhi.geektime.projects.user.domain;

import com.jizhi.geektime.projects.user.validator.annotation.PhoneNumber;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

//import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

//import static javax.persistence.GenerationType.AUTO;

/**
 * 用户领域对象
 *
 * @author jizhi7
 * @since 1.0
 **/
//@Entity
//@Table(name = "users")
public class User {

    //  @Id
    // @GeneratedValue(strategy = AUTO)
    @NotNull
    private Long id;

    //@Column
    @NotNull
    private String name;

    // @Column
    @Length(min = 6, max = 32)
    private String password;

    // @Column
    @Email
    private String email;

    // @Column
    @PhoneNumber
    private String phoneNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(name, user.name) &&
                Objects.equals(password, user.password) &&
                Objects.equals(email, user.email) &&
                Objects.equals(phoneNumber, user.phoneNumber);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, password, email, phoneNumber);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
