package ru.matthew.NauJava.service.report;

import ru.matthew.NauJava.entity.Report;


public interface ReportService {

    /**
     * Создает отчет асинхронно
     *
     * @return Возвращает id объекта созданного в базе данных
     */
    Long generateReportAsync();


    /**
     * Ищет отчет по его id
     *
     * @param id уникальный идентификатор отчета
     * @return найденный отчет типа {@link Report}
     */
    Report findById(Long id);
}
