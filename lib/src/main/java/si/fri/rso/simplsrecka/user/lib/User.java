package si.fri.rso.simplsrecka.user.lib;

import java.math.BigDecimal;
import java.time.Instant;

public class User {

    private Integer user_id;
    private String username;
    private String email;
    private String password;
    private String name;
    private String surname;
    private BigDecimal accountBalance;


    public Integer getUserId() {
        return user_id;
    }

    public void setUserId(Integer id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }

}
