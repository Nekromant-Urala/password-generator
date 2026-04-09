package ru.matthew.NauJava.service.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.matthew.NauJava.entity.PasswordEntry;
import ru.matthew.NauJava.entity.Report;
import ru.matthew.NauJava.entity.ReportStatus;
import ru.matthew.NauJava.repository.ReportRepository;
import ru.matthew.NauJava.repository.PasswordEntryRepository;
import ru.matthew.NauJava.repository.UserRepository;
import ru.matthew.NauJava.repository.exception.NotFoundReportException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PasswordEntryRepository passwordRepository;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository, UserRepository userRepository, PasswordEntryRepository passwordRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.passwordRepository = passwordRepository;
    }

    @Override
    @Transactional
    public Long generateOrderAsync() {
        Report report = new Report();
        report.setStatus(ReportStatus.GENERATED);
        report = reportRepository.save(report);
        final Long reportId = report.getId();

        CompletableFuture.runAsync(() -> {
            long totalStartTime = System.currentTimeMillis();
            try {
                final long[] userCount = {0};
                final long[] userTime = {0};

                final List<PasswordEntry> entriesList = new ArrayList<>();
                final long[] listTime = {0};

                Thread t1 = new Thread(() -> {
                    long start = System.currentTimeMillis();
                    userCount[0] = userRepository.count();
                    userTime[0] = System.currentTimeMillis() - start;
                });

                Thread t2 = new Thread(() -> {
                    long start = System.currentTimeMillis();
                    entriesList.addAll(passwordRepository.findAll());
                    listTime[0] = System.currentTimeMillis() - start;
                });

                t1.start();
                t2.start();

                t1.join();
                t2.join();

                long totalTime = System.currentTimeMillis() - totalStartTime;

                String htmlReport = buildHtmlReport(userCount[0], userTime[0], entriesList, listTime[0], totalTime);

                Report finishedReport = reportRepository.findById(reportId).orElseThrow(
                        () -> new NotFoundReportException("Отчет не найден")
                );
                finishedReport.setDescription(htmlReport);
                finishedReport.setStatus(ReportStatus.COMPLETED);
                reportRepository.save(finishedReport);

            } catch (Exception e) {
                Report errorReport = reportRepository.findById(reportId).get();
                errorReport.setDescription("Ошибка при формировании отчета: " + e.getMessage());
                errorReport.setStatus(ReportStatus.ERROR);
                reportRepository.save(errorReport);
            }
        });

        return reportId;
    }

    @Override
    @Transactional(readOnly = true)
    public Report findById(Long id) {
        return reportRepository.findById(id).orElseThrow(
                () -> new NotFoundReportException("Отчет не удалось найти. Заданный id: " + id)
        );
    }

    private String buildHtmlReport(long userCount, long userTime, List<PasswordEntry> entries, long listTime, long totalTime) {
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellspacing='0' cellpadding='5'>");
        html.append("<tr><th>Метрика</th><th>Значение</th><th>Затраченное время (мс)</th></tr>");
        html.append("<tr><td>Количество пользователей</td><td>").append(userCount).append("</td><td>").append(userTime).append("</td></tr>");
        html.append("<tr><td>Количество записей</td><td>").append(entries.size()).append("</td><td>").append(listTime).append("</td></tr>");
        html.append("<tr><td><b>Общее время</b></td><td>-</td><td><b>").append(totalTime).append("</b></td></tr>");
        html.append("</table>");

        html.append("<h3>Список информации о паролях</h3>");
        html.append("<table border='1' cellspacing='0' cellpadding='5'>");
        html.append("<tr><th>ID</th><th>Название сервиса</th><th>Описание</th></tr>");

        for (var entry : entries) {
            html.append("<tr>");
            html.append("<td>").append(entry.getId()).append("</td>");
            html.append("<td>").append(entry.getServiceName()).append("</td>");
            html.append("<td>").append(entry.getDescription()).append("</td>");
            html.append("</tr>");
        }

        html.append("</table></body></html>");
        return html.toString();
    }
}
