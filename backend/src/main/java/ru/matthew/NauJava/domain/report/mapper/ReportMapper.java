package ru.matthew.NauJava.domain.report.mapper;

import org.springframework.stereotype.Component;
import ru.matthew.NauJava.domain.report.Report;
import ru.matthew.NauJava.domain.report.dto.ReportDto;

@Component
public class ReportMapper {

    public Report toReport(ReportDto dto){
        if (dto == null) {
            return null;
        }

        Report report = new Report();
        report.setId(dto.id());
        report.setStatus(dto.status());
        report.setCreatedAt(dto.createdAt());
        report.setDescription(dto.description());
        return report;
    }

    public ReportDto toReportDto(Report report) {
        if (report == null) {
            return null;
        }
        return new ReportDto(
                report.getId(),
                report.getStatus(),
                report.getCreatedAt(),
                report.getDescription()
        );
    }
}
