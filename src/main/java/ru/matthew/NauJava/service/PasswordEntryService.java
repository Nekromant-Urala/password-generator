package ru.matthew.NauJava.service;

import ru.matthew.NauJava.entity.PasswordEntry;
import ru.matthew.NauJava.entity.User;

import java.util.List;
import java.util.Optional;

public interface PasswordEntryService {

    /**
     * Создает новую запись пароля в хранилище
     *
     * @param login       логин пользователя для сервиса
     * @param password    пароль для доступа к сервису
     * @param description дополнительное описание записи
     * @param serviceName наименование сервиса
     * @param user        Объект типа {@link User} содержащий информацию о пользователе
     */
    void createPasswordEntry(String login, String password, String description, String serviceName, User user);

    /**
     * Находит запись пароля по её уникальному идентификатору
     *
     * @param id уникальный идентификатор записи
     * @return найденная запись
     */
    Optional<PasswordEntry> findById(Long id);

    /**
     * Находит все записи паролей для указанного сервиса
     *
     * @param serviceName название сервиса или приложения
     * @return список записей паролей для указанного сервиса
     */
    List<PasswordEntry> findByServiceName(String serviceName);

    /**
     * Возвращает список всех записей паролей в хранилище.
     *
     * @return список всех записей-паролей
     */
    List<PasswordEntry> findAll();

    /**
     * Удаляет запись пароля по её уникальному идентификатору
     *
     * @param id уникальный идентификатор записи
     */
    void deleteById(Long id);

    /**
     * Удаляет все записи паролей для указанного сервиса
     *
     * @param serviceName название сервиса или приложения
     */
    void deleteByServiceName(String serviceName);

    /**
     * Обновляет пароль для существующей записи
     *
     * @param id          уникальный идентификатор записи
     * @param newPassword новый пароль
     */
    void updatePassword(Long id, String newPassword);

    /**
     * Обновляет логин для существующей записи
     *
     * @param id       уникальный идентификатор записи
     * @param newLogin новый логин
     */
    void updateLogin(Long id, String newLogin);

    /**
     * Обновляет название сервиса для существующей записи
     *
     * @param id             уникальный идентификатор записи
     * @param newServiceName новое название сервиса или приложения
     */
    void updateServiceName(Long id, String newServiceName);

    /**
     * Обновляет описание для существующей записи
     *
     * @param id             уникальный идентификатор записи
     * @param newDescription новое описание
     */
    void updateDescription(Long id, String newDescription);
}
