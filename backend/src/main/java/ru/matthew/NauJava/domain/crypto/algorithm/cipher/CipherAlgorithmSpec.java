package ru.matthew.NauJava.domain.crypto.algorithm.cipher;

public enum CipherAlgorithmSpec implements SymmetricalAlgorithmSpec {
    AES(
            "AES",
            "AES/GCM/NoPadding",
            256,
            128,
            12,
            16
    ),
    CHACHA20(
            "ChaCha20",
            "ChaCha20-Poly1305",
            256,
            128,
            12,
            12
    ),
    TWOFISH(
            "Twofish",
            "TwoFish/GCM/NoPadding",
            256,
            128,
            12,
            16
    );

    private final String name;
    private final String mode;
    private final int keyLengthBit;
    private final int tagLengthBit;
    private final int ivLengthByte;
    private final int saltLengthByte;

    CipherAlgorithmSpec(String name, String mode, int keyLengthBit, int tagLengthBit, int ivLengthByte, int saltLengthByte) {
        this.name = name;
        this.mode = mode;
        this.keyLengthBit = keyLengthBit;
        this.tagLengthBit = tagLengthBit;
        this.ivLengthByte = ivLengthByte;
        this.saltLengthByte = saltLengthByte;
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
    public int getKeyLengthBit() {
        return keyLengthBit;
    }

    @Override
    public int getTagLengthBit() {
        return tagLengthBit;
    }

    @Override
    public int getIvLengthByte() {
        return ivLengthByte;
    }

    @Override
    public int getSaltLengthByte() {
        return saltLengthByte;
    }
}
