package ru.matthew.NauJava.service.report;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.matthew.NauJava.entity.Report;
import ru.matthew.NauJava.entity.ReportStatus;
import ru.matthew.NauJava.repository.PasswordEntryRepository;
import ru.matthew.NauJava.repository.ReportRepository;
import ru.matthew.NauJava.repository.UserRepository;
import ru.matthew.NauJava.repository.exception.NotFoundReportException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ReportServiceImplTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEntryRepository passwordRepository;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    @DisplayName("Успешная генерация отчета")
    void testGenerateReportAsync_Success() {
        var report = initReport();
        report.setStatus(ReportStatus.GENERATED);

        when(reportRepository.save(any(Report.class))).thenReturn(report);
        when(userRepository.count()).thenReturn(10L);
        when(passwordRepository.findAll()).thenReturn(Collections.emptyList());
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Отчет сформирован</html>");
        when(reportRepository.findById(report.getId())).thenReturn(Optional.of(report));

        Long actualReportId = reportService.generateReportAsync();

        assertEquals(report.getId(), actualReportId);

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository, timeout(2000).times(2)).save(reportCaptor.capture());

        Report actualReport = reportCaptor.getAllValues().get(1);

        assertEquals(ReportStatus.COMPLETED, actualReport.getStatus());
        assertEquals("<html>Отчет сформирован</html>", actualReport.getDescription());
    }

    @Test
    @DisplayName("Ошибка при генерации отчета")
    void testGenerateReportAsync_Runtime_ThrowsException() {
        var report = initReport();
        report.setStatus(ReportStatus.GENERATED);

        when(reportRepository.save(any(Report.class))).thenReturn(report);
        when(reportRepository.findById(report.getId())).thenReturn(Optional.of(report));
        when(userRepository.count()).thenReturn(10L);
        when(passwordRepository.findAll()).thenReturn(Collections.emptyList());
        when(templateEngine.process(anyString(), any(Context.class))).thenThrow(new RuntimeException("RuntimeException"));

        Long actualReportId = reportService.generateReportAsync();

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository, timeout(2000).times(2)).save(reportCaptor.capture());

        Report actualReport = reportCaptor.getAllValues().get(1);

        assertEquals(ReportStatus.ERROR, actualReport.getStatus());
        assertTrue(actualReport.getDescription().contains("Ошибка при формировании отчета: RuntimeException"));
    }

    @Test
    @DisplayName("Удачное нахождение отчета по ID")
    void testFindById_Success() {
        var report = initReport();
        report.setStatus(ReportStatus.COMPLETED);

        when(reportRepository.findById(report.getId())).thenReturn(Optional.of(report));

        var actualReport = reportService.findById(report.getId());

        assertEquals(report.getId(), actualReport.getId());
        assertEquals(report.getStatus(), actualReport.getStatus());
    }

    @Test
    @DisplayName("Отсутствие отчета при поиске по ID и выбрасывание соответствующей ошибки")
    void testFindById_NotFoundReport_ThrowsException() {
        var report = initReport();
        report.setStatus(ReportStatus.COMPLETED);

        when(reportRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundReportException.class, () -> reportService.findById(report.getId()));
    }

    private Report initReport() {
        Long reportId = 1L;
        Report report = new Report();
        report.setId(reportId);
        return report;
    }
}
