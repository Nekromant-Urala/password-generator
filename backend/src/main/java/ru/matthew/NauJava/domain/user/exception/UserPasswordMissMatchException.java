package ru.matthew.NauJava.domain.user.exception;

public class UserPasswordMissMatchException extends RuntimeException {
    public UserPasswordMissMatchException(String message) {
        super(message);
    }
}
