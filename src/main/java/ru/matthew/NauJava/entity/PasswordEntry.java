package ru.matthew.NauJava.entity;


import java.time.LocalDateTime;
import java.util.Objects;

public class PasswordEntry {
    private Long id;
    private String login;
    private String password;
    private String serviceName;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String description;

    public PasswordEntry() {
        created_at = LocalDateTime.now();
    }

    public PasswordEntry(PasswordEntry other) {
        this.id = other.id;
        this.login = other.login;
        this.password = other.password;
        this.serviceName = other.serviceName;
        this.created_at = other.created_at;
        this.updated_at = other.updated_at;
        this.description = other.description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PasswordEntry that = (PasswordEntry) o;
        return Objects.equals(id, that.id) && Objects.equals(login, that.login) && Objects.equals(password, that.password) && Objects.equals(serviceName, that.serviceName) && Objects.equals(created_at, that.created_at) && Objects.equals(updated_at, that.updated_at) && Objects.equals(description, that.description);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, login, password, serviceName, created_at, updated_at, description);
    }

    @Override
    public String toString() {
        return "PasswordEntry{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", description='" + description + '\'' +
                '}';
    }
}
