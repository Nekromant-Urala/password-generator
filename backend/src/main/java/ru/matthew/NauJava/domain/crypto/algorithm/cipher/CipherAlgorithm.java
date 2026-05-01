package ru.matthew.NauJava.domain.crypto.algoritm.cipher;

import jakarta.persistence.*;

@Entity
@Table(name = "cipher_algorithms")
public class CipherAlgorithm {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "key_length_min")
    private Integer keyLengthMin;

    @Column(name = "key_length_max")
    private Integer keyLengthMax;

    @Column(name = "key_length_default")
    private Integer keyLengthDefault;

    @Column(name = "iv_length")
    private Integer ivLength;

    @Column(name = "is_authenticated")
    private boolean isAuthenticated;

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

    public Integer getKeyLengthMin() {
        return keyLengthMin;
    }

    public void setKeyLengthMin(Integer keyLengthMin) {
        this.keyLengthMin = keyLengthMin;
    }

    public Integer getKeyLengthMax() {
        return keyLengthMax;
    }

    public void setKeyLengthMax(Integer keyLengthMax) {
        this.keyLengthMax = keyLengthMax;
    }

    public Integer getKeyLengthDefault() {
        return keyLengthDefault;
    }

    public void setKeyLengthDefault(Integer keyLengthDefault) {
        this.keyLengthDefault = keyLengthDefault;
    }

    public Integer getIvLength() {
        return ivLength;
    }

    public void setIvLength(Integer ivLength) {
        this.ivLength = ivLength;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }
}
