package ru.matthew.NauJava.domain.user.exception;

public class UserNotFoundException extends RuntimeException {

    private static final String STANDARD_MESSAGE = "Пользователь по заданному id: '%d' не был найден.";

    public UserNotFoundException(long id) {
        super(STANDARD_MESSAGE.formatted(id));
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
