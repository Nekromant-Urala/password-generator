package ru.matthew.NauJava.domain.user;

import jakarta.persistence.*;
import ru.matthew.NauJava.domain.password.PasswordEntry;
import ru.matthew.NauJava.domain.profile.GeneratorProfile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность пользователя системы.
 * Содержит учетные данные и управляет связями с профилями и записями паролей.
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PasswordEntry> passwordEntries = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GeneratorProfile> profiles = new ArrayList<>();

    public User() {
        createdAt = LocalDateTime.now();
    }

    public void addPasswordEntries(PasswordEntry passwordEntry) {
        passwordEntries.add(passwordEntry);
        passwordEntry.setUser(this);
    }

    public void removePasswordEntry(PasswordEntry passwordEntry) {
        passwordEntries.remove(passwordEntry);
        passwordEntry.setUser(null);
    }

    public void addProfile(GeneratorProfile profile) {
        profiles.add(profile);
        profile.setUser(this);
    }

    public void removeProfile(GeneratorProfile profile) {
        profiles.remove(profile);
        profile.setUser(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime created_at) {
        this.createdAt = created_at;
    }
}
