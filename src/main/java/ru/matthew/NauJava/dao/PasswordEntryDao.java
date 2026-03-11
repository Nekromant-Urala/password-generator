package ru.matthew.NauJava.dao;

import java.util.List;

public interface PasswordEntryDao<T, ID> {

    /**
     * Сохраняет запись в коллекцию/бд
     *
     * @param entity запись, которую необходимо сохранить
     */
    void save(T entity);

    /**
     * Находит запись по указанному {@code id}
     *
     * @param id идентификатор записи
     * @return Возвращает запись по заданному {@code id}
     */
    T findById(ID id);

    /**
     * Находит все записи с указанным {@code serviceName}
     *
     * @param serviceName наименование сервиса в записи
     * @return Возвращает список всех записей с указанным {@code serviceName}
     */
    List<T> findByServiceName(String serviceName);

    /**
     * Находит все добавленные записи в коллекцию/бд
     *
     * @return Возвращает список всех записей
     */
    List<T> findAll();

    /**
     * Обновление записи
     *
     * @param entity запись, которую нужно обновить
     */
    void update(T entity);

    /**
     * Удаление записи по ID
     *
     * @param id идентификатор записи
     */
    void deleteById(ID id);

    /**
     * Удаление всех записей содержащих указанный {@code serviceName}
     *
     * @param serviceName наименование сервиса в записи
     */
    void deleteByServiceName(String serviceName);
}
