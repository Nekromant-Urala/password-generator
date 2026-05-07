package ru.matthew.NauJava.domain.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.matthew.NauJava.domain.report.dto.ReportDto;
import ru.matthew.NauJava.domain.report.exception.ReportNotFoundException;
import ru.matthew.NauJava.domain.report.mapper.ReportMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository, ReportMapper reportMapper) {
        this.reportRepository = reportRepository;
        this.reportMapper = reportMapper;
    }

    @Override
    public ReportDto createReport() {
        //TODO добавить генерацию отчета
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDto findById(Long id) {
        return reportRepository.findById(id)
                .map(reportMapper::toReportDto)
                .orElseThrow(
                        () -> new ReportNotFoundException("Отчет с id: '%d' не был найден".formatted(id))
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDto> findByStatus(ReportStatus status) {
        return reportRepository.findByStatus(status).stream()
                .map(reportMapper::toReportDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDto> findByCreatedAt(LocalDateTime createdAt) {
        return reportRepository.findByCreatedAt(createdAt).stream()
                .map(reportMapper::toReportDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDto> findAll() {
        return reportRepository.findAll().stream()
                .map(reportMapper::toReportDto)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        reportRepository.deleteById(id);
    }

    @Override
    public void deleteByStatus(ReportStatus status) {
        reportRepository.deleteByStatus(status);
    }

    @Override
    public void deleteByCreatedAt(LocalDateTime createdAt) {
        reportRepository.deleteByCreatedAt(createdAt);
    }

    @Override
    public void deleteByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        reportRepository.deleteByCreatedAtBetween(startDate, endDate);
    }
}
