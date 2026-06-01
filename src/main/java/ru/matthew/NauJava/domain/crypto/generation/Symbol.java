package ru.matthew.NauJava.domain.crypto.generation;

public enum Symbol {
    LOWERCASE("abcdefghijklmnopqrstuvwxyz"),
    UPPERCASE("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    DIGITS("0123456789"),
    SPECIAL_CHARS("!@#$%^&*()-_=+[{]}");

    private final String chars;

    Symbol(String chars) {
        this.chars = chars;
    }

    public String getChars() {
        return chars;
    }
}
