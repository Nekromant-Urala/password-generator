package ru.matthew.NauJava.service.order;

import ru.matthew.NauJava.entity.Report;

import java.util.Optional;


public interface ReportService {

    /**
     * Создает отчет асинхронно
     *
     * @return Возвращает id объекта созданного в базе данных
     */
    Long generateOrderAsync();


    /**
     * Ищет отчет по его id
     *
     * @param id уникальный идентификатор отчета
     * @return {@link Optional} с найденным отчетом, или {@link Optional#empty()}, если отчет не найден
     */
    //TODO поправить java-doc
    Report findById(Long id);
}
