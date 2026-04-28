package ru.matthew.NauJava.domain.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
public class ReportRestController {

    private final ReportService reportService;

    @Autowired
    public ReportRestController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generate")
    public ResponseEntity<Long> generateReport() {
        Long reportId = reportService.generateReportAsync();
        return ResponseEntity.ok(reportId);
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<Report> getReportContent(@PathVariable Long id) {
        Report report = reportService.findById(id);
        return ResponseEntity.ok(report);
    }
}
