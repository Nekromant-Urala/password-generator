package ru.matthew.NauJava.domain.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import ru.matthew.NauJava.domain.password.PasswordEntry;
import ru.matthew.NauJava.domain.password.PasswordEntryRepository;
import ru.matthew.NauJava.domain.user.UserRepository;
import ru.matthew.NauJava.domain.report.exception.NotFoundReportException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PasswordEntryRepository passwordRepository;
    private final TemplateEngine templateEngine;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository, UserRepository userRepository, PasswordEntryRepository passwordRepository, TemplateEngine templateEngine) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.passwordRepository = passwordRepository;
        this.templateEngine = templateEngine;
    }

    @Override
    @Transactional
    public Long generateReportAsync() {
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

                String htmlReport = buildHtmlTableReport(userCount[0], userTime[0], entriesList, listTime[0], totalTime);

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

    private String buildHtmlTableReport(long userCount, long userTime, List<PasswordEntry> entries, long listTime, long totalTime) {
        Context ctx = new Context();
        ctx.setVariable("userCount", userCount);
        ctx.setVariable("userTime", userTime);
        ctx.setVariable("entries", entries);
        ctx.setVariable("listTime", listTime);
        ctx.setVariable("totalTime", totalTime);

        return templateEngine.process("details\\report-table.html", ctx);
    }
}
