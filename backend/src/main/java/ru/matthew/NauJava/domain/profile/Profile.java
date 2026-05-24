package ru.matthew.NauJava.domain.profile;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.KdfAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.KdfAlgorithmSpecConverter;
import ru.matthew.NauJava.domain.user.User;

import java.time.LocalDateTime;

//TODO при удалении сущности, записи зашифрованные с помощью него будут не доступны (если изменяться параметры итерации или алгоритмов)
@Entity
@Table(name = "generator_profile")
public class Profile {

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
    private boolean isDuplicateChars;

    @Column(name = "is_favorite")
    private boolean isFavorite;

    @Column(name = "custom_chars")
    private String customChars;

    @CreationTimestamp
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "kdf_algorithm")
    @Convert(converter = KdfAlgorithmSpecConverter.class)
    private KdfAlgorithmSpec kdfAlgorithm;

    @Column(name = "cipher_algorithm")
    @Enumerated(value = EnumType.STRING)
    private CipherAlgorithmSpec cipher;

    @Column(name = "iterations")
    private Integer iterations;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    protected Profile() {
    }

    public Profile(GeneratorProfileBuilder builder) {
        this.name = builder.name;
        this.passwordLength = builder.passwordLength;
        this.isUppercase = builder.isUppercase;
        this.isLowercase = builder.isLowercase;
        this.isDigits = builder.isDigits;
        this.isSpecialChars = builder.isSpecialChars;
        this.isDuplicateChars = builder.isAvoidAmbiguousChars;
        this.isFavorite = builder.isFavorite;
        this.customChars = builder.customChars;
        this.kdfAlgorithm = builder.kdfAlgorithm;
        this.cipher = builder.cipher;
        this.iterations = builder.iterations;
    }

    public static class GeneratorProfileBuilder {
        private String name;
        private Integer passwordLength;
        private boolean isUppercase;
        private boolean isLowercase;
        private boolean isDigits;
        private boolean isSpecialChars;
        private boolean isAvoidAmbiguousChars;
        private boolean isFavorite;
        private String customChars;
        private KdfAlgorithmSpec kdfAlgorithm;
        private CipherAlgorithmSpec cipher;
        private Integer iterations;

        public GeneratorProfileBuilder name(String name) {
            this.name = name;
            return this;
        }

        public GeneratorProfileBuilder passwordLength(Integer passwordLength) {
            this.passwordLength = passwordLength;
            return this;
        }

        public GeneratorProfileBuilder uppercase(boolean uppercase) {
            isUppercase = uppercase;
            return this;
        }

        public GeneratorProfileBuilder lowercase(boolean lowercase) {
            isLowercase = lowercase;
            return this;
        }

        public GeneratorProfileBuilder digits(boolean digits) {
            isDigits = digits;
            return this;
        }

        public GeneratorProfileBuilder specialChars(boolean specialChars) {
            isSpecialChars = specialChars;
            return this;
        }

        public GeneratorProfileBuilder avoidAmbiguousChars(boolean avoidAmbiguousChars) {
            isAvoidAmbiguousChars = avoidAmbiguousChars;
            return this;
        }

        public GeneratorProfileBuilder favorite(boolean favorite) {
            isFavorite = favorite;
            return this;
        }

        public GeneratorProfileBuilder customChars(String customChars) {
            this.customChars = customChars;
            return this;
        }

        public GeneratorProfileBuilder kdfAlgorithm(KdfAlgorithmSpec kdfAlgorithm) {
            this.kdfAlgorithm = kdfAlgorithm;
            return this;
        }

        public GeneratorProfileBuilder cipher(CipherAlgorithmSpec cipher) {
            this.cipher = cipher;
            return this;
        }

        public GeneratorProfileBuilder iterations(Integer iterations) {
            this.iterations = iterations;
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

    public Integer getIterations() {
        return iterations;
    }

    public void setIterations(Integer iterations) {
        this.iterations = iterations;
    }
}
