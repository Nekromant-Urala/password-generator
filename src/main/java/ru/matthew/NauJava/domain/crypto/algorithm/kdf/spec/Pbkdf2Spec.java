package ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec;

public enum Pbkdf2Spec implements Pbkdf2Configuration {
    PBKDF_2(
            "PBKDF2",
            "PBKDF2WithHmacSHA512",
            256,
            210_000
    );

    private final String name;
    private final String mode;
    private final int keyLengthBit;
    private final int iterations;

    Pbkdf2Spec(String name, String mode, int keyLengthBit, int iterations) {
        this.name = name;
        this.mode = mode;
        this.keyLengthBit = keyLengthBit;
        this.iterations = iterations;
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

    @Override
    public int getIterations() {
        return iterations;
    }
}
