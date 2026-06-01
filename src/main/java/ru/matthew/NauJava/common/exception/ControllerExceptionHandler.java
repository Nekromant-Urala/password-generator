package ru.matthew.NauJava.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ru.matthew.NauJava.domain.audit.exception.AuditEventNotFoundException;
import ru.matthew.NauJava.domain.crypto.exception.CipherNotFoundException;
import ru.matthew.NauJava.domain.crypto.exception.EncryptionException;
import ru.matthew.NauJava.domain.crypto.exception.KdfNotFoundException;
import ru.matthew.NauJava.domain.password.exception.PasswordEntryDecodeException;
import ru.matthew.NauJava.domain.profile.exception.ProfileNotFoundException;
import ru.matthew.NauJava.domain.password.exception.PasswordEntryNotFoundException;
import ru.matthew.NauJava.domain.user.exception.UserAlreadyExistsException;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;
import ru.matthew.NauJava.domain.user.exception.UserPasswordMissMatchException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            PasswordEntryNotFoundException.class,
            ProfileNotFoundException.class, UserNotFoundException.class,
            org.springframework.security.core.userdetails.UsernameNotFoundException.class,
            AuditEventNotFoundException.class
    })
    public ErrorResponse handleNotFound(RuntimeException exception) {
        LOGGER.warn("Ресурс не был найден: {}", exception.getMessage());
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ErrorResponse handleUserAlreadyExists(RuntimeException exception) {
        LOGGER.warn("Пользователь с заданными параметрами уже существует: {}", exception.getMessage());
        return new ErrorResponse(HttpStatus.CONFLICT.value(), exception.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({UserPasswordMissMatchException.class, IllegalArgumentException.class})
    public ErrorResponse handleBadRequest(RuntimeException exception) {
        LOGGER.warn("Некорректный запрос (Bad Request): {}", exception.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(PasswordEntryDecodeException.class)
    public ErrorResponse handlePasswordEntryDecode(RuntimeException exception) {
        LOGGER.error("Ошибка при генерации пароля:", exception);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ошибка при генерации пароля");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            CipherNotFoundException.class, EncryptionException.class, KdfNotFoundException.class
    })
    public ErrorResponse handleEncryptionException(RuntimeException exception) {
        LOGGER.error("Ошибка при шифровании/расшифровывании данных:", exception);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ошибка при шифровании/расшифровывании данных");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleOtherException(Exception exception) {
        LOGGER.error("Внутренняя ошибка сервера:", exception);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Внутренняя ошибка сервера");
    }
}
