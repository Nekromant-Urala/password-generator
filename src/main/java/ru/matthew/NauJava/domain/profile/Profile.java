package ru.matthew.NauJava.domain.profile;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.KdfAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.KdfAlgorithmSpecConverter;
import ru.matthew.NauJava.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "generator_profile")
public class Profile {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "password_length", nullable = false)
    private Integer passwordLength;

    @Column(name = "include_uppercase", nullable = false)
    private boolean isUppercase;

    @Column(name = "include_lowercase", nullable = false)
    private boolean isLowercase;

    @Column(name = "include_digits", nullable = false)
    private boolean isDigits;

    @Column(name = "include_special_chars", nullable = false)
    private boolean isSpecialChars;

    @Column(name = "avoid_ambiguous_chars", nullable = false)
    private boolean isDuplicateChars;

    @Column(name = "is_favorite")
    private boolean isFavorite;

    @Column(name = "custom_chars")
    private String customChars;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false, nullable = false)
    private LocalDateTime createAt;

    @Column(name = "kdf_algorithm", nullable = false)
    @Convert(converter = KdfAlgorithmSpecConverter.class)
    private KdfAlgorithmSpec kdfAlgorithm;

    @Column(name = "cipher_algorithm", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CipherAlgorithmSpec cipher;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    protected Profile() {
    }

    public Profile(ProfileBuilder builder) {
        this.name = builder.name;
        this.passwordLength = builder.passwordLength;
        this.isUppercase = builder.isUppercase;
        this.isLowercase = builder.isLowercase;
        this.isDigits = builder.isDigits;
        this.isSpecialChars = builder.isSpecialChars;
        this.isDuplicateChars = builder.isDuplicateChars;
        this.isFavorite = builder.isFavorite;
        this.customChars = builder.customChars;
        this.kdfAlgorithm = builder.kdfAlgorithm;
        this.cipher = builder.cipher;
    }

    public static class ProfileBuilder {
        private String name;
        private Integer passwordLength;
        private boolean isUppercase;
        private boolean isLowercase;
        private boolean isDigits;
        private boolean isSpecialChars;
        private boolean isDuplicateChars;
        private boolean isFavorite;
        private String customChars;
        private KdfAlgorithmSpec kdfAlgorithm;
        private CipherAlgorithmSpec cipher;

        public ProfileBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProfileBuilder passwordLength(Integer passwordLength) {
            this.passwordLength = passwordLength;
            return this;
        }

        public ProfileBuilder uppercase(boolean uppercase) {
            isUppercase = uppercase;
            return this;
        }

        public ProfileBuilder lowercase(boolean lowercase) {
            isLowercase = lowercase;
            return this;
        }

        public ProfileBuilder digits(boolean digits) {
            isDigits = digits;
            return this;
        }

        public ProfileBuilder specialChars(boolean specialChars) {
            isSpecialChars = specialChars;
            return this;
        }

        public ProfileBuilder duplicateChars(boolean isDuplicate) {
            isDuplicateChars = isDuplicate;
            return this;
        }

        public ProfileBuilder favorite(boolean favorite) {
            isFavorite = favorite;
            return this;
        }

        public ProfileBuilder customChars(String customChars) {
            this.customChars = customChars;
            return this;
        }

        public ProfileBuilder kdfAlgorithm(KdfAlgorithmSpec kdfAlgorithm) {
            this.kdfAlgorithm = kdfAlgorithm;
            return this;
        }

        public ProfileBuilder cipher(CipherAlgorithmSpec cipher) {
            this.cipher = cipher;
            return this;
        }

        public Profile build() {
            return new Profile(this);
        }
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

    public boolean isDuplicateChars() {
        return isDuplicateChars;
    }

    public void setDuplicateChars(boolean duplicateChars) {
        isDuplicateChars = duplicateChars;
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

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public KdfAlgorithmSpec getKdfAlgorithm() {
        return kdfAlgorithm;
    }

    public void setKdfAlgorithm(KdfAlgorithmSpec kdfAlgorithm) {
        this.kdfAlgorithm = kdfAlgorithm;
    }

    public CipherAlgorithmSpec getCipher() {
        return cipher;
    }

    public void setCipher(CipherAlgorithmSpec cipher) {
        this.cipher = cipher;
    }
}
