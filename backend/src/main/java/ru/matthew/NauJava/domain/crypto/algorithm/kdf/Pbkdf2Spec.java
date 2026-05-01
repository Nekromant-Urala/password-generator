package ru.matthew.NauJava.domain.crypto.algorithm.kdf;

public enum Pbkdf2Spec implements Pbkdf2Configuration {
    PBKDF_2(
            "PBKDF2",
            "PBKDF2WithHmacSHA256",
            256
    );

    private final String name;
    private final String mode;
    private final int keyLengthBit;

    Pbkdf2Spec(String name, String mode, int keyLengthBit) {
        this.name = name;
        this.mode = mode;
        this.keyLengthBit = keyLengthBit;
    }

    @Override
    public int getKeyLengthBit() {
        return keyLengthBit;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getMode() {
        return mode;
    }
}
