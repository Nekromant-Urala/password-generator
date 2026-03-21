package ru.matthew.NauJava.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "generator_profile")
public class GeneratorProfile {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "password_length")
    private Integer passwordLength;

    @Column(name = "include_uppercase")
    private boolean isUppercase;

    @Column(name = "include_lowercase")
    private boolean isLowercase;

    @Column(name = "include_digits")
    private boolean isDigits;

    @Column(name = "include_special_chars")
    private boolean isSpecialChars;

    @Column(name = "avoid_ambiguous_chars")
    private boolean isAvoidAmbiguousChars;

    @Column(name = "is_favorite")
    private boolean isFavorite;

    @Column(name = "custom_chars")
    private String customChars;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "kdf_algorithm_id")
    private KdfAlgorithm kdfAlgorithm;

    @ManyToOne
    @JoinColumn(name = "algorithm_id")
    private CipherAlgorithm cipher;

    public GeneratorProfile() {
        createAt = LocalDateTime.now();
    }

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

    public Integer getPasswordLength() {
        return passwordLength;
    }

    public void setPasswordLength(Integer passwordLength) {
        this.passwordLength = passwordLength;
    }

    public boolean isUppercase() {
        return isUppercase;
    }

    public void setUppercase(boolean uppercase) {
        isUppercase = uppercase;
    }

    public boolean isLowercase() {
        return isLowercase;
    }

    public void setLowercase(boolean lowercase) {
        isLowercase = lowercase;
    }

    public boolean isDigits() {
        return isDigits;
    }

    public void setDigits(boolean digits) {
        isDigits = digits;
    }

    public boolean isSpecialChars() {
        return isSpecialChars;
    }

    public void setSpecialChars(boolean specialChars) {
        isSpecialChars = specialChars;
    }

    public boolean isAvoidAmbiguousChars() {
        return isAvoidAmbiguousChars;
    }

    public void setAvoidAmbiguousChars(boolean avoidAmbiguousChars) {
        isAvoidAmbiguousChars = avoidAmbiguousChars;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getCustomChars() {
        return customChars;
    }

    public void setCustomChars(String customChars) {
        this.customChars = customChars;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime create_at) {
        this.createAt = create_at;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public KdfAlgorithm getKdfAlgorithm() {
        return kdfAlgorithm;
    }

    public void setKdfAlgorithm(KdfAlgorithm kdfAlgorithm) {
        this.kdfAlgorithm = kdfAlgorithm;
    }

    public CipherAlgorithm getCipher() {
        return cipher;
    }

    public void setCipher(CipherAlgorithm cipher) {
        this.cipher = cipher;
    }

    @Override
    public String toString() {
        return "GeneratorProfile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", passwordLength=" + passwordLength +
                ", isUppercase=" + isUppercase +
                ", isLowercase=" + isLowercase +
                ", isDigits=" + isDigits +
                ", isSpecialChars=" + isSpecialChars +
                ", isAvoidAmbiguousChars=" + isAvoidAmbiguousChars +
                ", isFavorite=" + isFavorite +
                ", customChars='" + customChars + '\'' +
                ", createAt=" + createAt +
                ", user=" + user +
                ", kdfAlgorithm=" + kdfAlgorithm +
                ", cipher=" + cipher +
                '}';
    }
}
