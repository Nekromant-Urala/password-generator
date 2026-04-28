package ru.matthew.NauJava.controller.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;
import ru.matthew.NauJava.config.SecurityConfig;
import ru.matthew.NauJava.domain.report.Report;
import ru.matthew.NauJava.domain.report.ReportRestController;
import ru.matthew.NauJava.domain.report.ReportService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest(ReportRestController.class)
public class ReportRestControllerTest {

    private static final String POST_URL = "/reports/generate";
    private static final String GET_URL = "/reports/";

    @MockitoBean
    private ReportService reportService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = "USER")
    public void testGenerateReport_Forbidden() throws Exception {
        when(reportService.generateReportAsync()).thenReturn(1L);

        mockMvc.perform(post(POST_URL).with(csrf())).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testGenerateReport_Success() throws Exception {
        when(reportService.generateReportAsync()).thenReturn(1L);

        mockMvc.perform(post(POST_URL).with(csrf())).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testGenerateReport_MethodNotAllowed() throws Exception {
        when(reportService.generateReportAsync()).thenReturn(1L);

        mockMvc.perform(get(POST_URL).with(csrf())).andExpect(status().isMethodNotAllowed());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testGetReportContent_Success() throws Exception {
        var report = new Report();
        Long reportId = 1L;
        report.setId(reportId);

        when(reportService.findById(reportId)).thenReturn(report);

        mockMvc.perform(get(GET_URL + reportId).with(csrf())).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testGetReportContent_InvalidFormat() throws Exception {
        String noDigits = "test";
        mockMvc.perform(get(GET_URL + noDigits).with(csrf())).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testGetReportContent_InternalServerError() throws Exception {
        when(reportService.findById(anyLong())).thenThrow(new RuntimeException("some error"));

        mockMvc.perform(get(GET_URL + anyLong()).with(csrf())).andExpect(status().isInternalServerError());
    }
}