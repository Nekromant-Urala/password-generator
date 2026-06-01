package ru.matthew.NauJava.domain.profile.exception;

public class ProfileNotFoundException extends RuntimeException {

    private static final String STANDARD_MESSAGE = "Профайл с настройками по заданному id: '%d' не был найден.";

    public ProfileNotFoundException(long id) {
        super(STANDARD_MESSAGE.formatted(id));
    }

    public ProfileNotFoundException(String message) {
        super(message);
    }
}
