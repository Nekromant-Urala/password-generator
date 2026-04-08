package ru.matthew.NauJava.controller.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.matthew.NauJava.entity.Report;
import ru.matthew.NauJava.repository.exception.NotFoundReportException;
import ru.matthew.NauJava.service.order.ReportService;

@Controller
@RequestMapping("reports/view")
public class ReportViewController {
    private final ReportService reportService;

    @Autowired
    public ReportViewController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String getPage() {
        return "details/reports";
    }

    @PostMapping("/create")
    public String createReport(Model model) {
        Long reportId = reportService.generateOrderAsync();
        model.addAttribute("message", "Запущена генерация отчета. ID вашего отчета: " + reportId);
        return "details/reports";
    }

    @GetMapping("/details")
    public String getReportDetails(@RequestParam(name = "id", required = false) Long id, Model model) {
        if (id == null) {
            return "details/reports";
        }

        try {
            Report report = reportService.findById(id);
            model.addAttribute("report", report);
            model.addAttribute("searchedId", id);
        } catch (NotFoundReportException e) {
            model.addAttribute("notFound", true);
            model.addAttribute("searchedId", id);
        }

        return "details/reports";
    }
}
