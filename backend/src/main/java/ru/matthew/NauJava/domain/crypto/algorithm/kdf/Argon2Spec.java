package ru.matthew.NauJava.domain.crypto.algorithm.kdf;

public enum Argon2Spec implements Argon2Configuration {
    ARGON_2(
            "Argon2",
            "Argon2i",
            131_072,
            32,
            2
    );

    private final String name;
    private final String mode;
    private final int memoryLimit;
    private final int parallelism;
    private final int hashLength;

    Argon2Spec(String name, String mode, int memoryLimit, int parallelism, int hashLength) {
        this.name = name;
        this.mode = mode;
        this.memoryLimit = memoryLimit;
        this.parallelism = parallelism;
        this.hashLength = hashLength;
    }

    @Override
    public int getMemoryLimit() {
        return memoryLimit;
    }

    @Override
    public int getHashLength() {
        return hashLength;
    }

    @Override
    public int getParallelism() {
        return parallelism;
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
