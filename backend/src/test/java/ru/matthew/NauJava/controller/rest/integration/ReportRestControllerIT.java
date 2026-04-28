package ru.matthew.NauJava.controller.rest.integration;

import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.context.jdbc.Sql;
import ru.matthew.NauJava.domain.report.Report;
import ru.matthew.NauJava.domain.report.exception.NotFoundReportException;
import ru.matthew.NauJava.domain.report.ReportService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;


@ActiveProfiles("test")
@Sql("/sql/create-user.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportRestControllerIT {

    @LocalServerPort
    private int port;

    @MockitoBean
    private ReportService reportService;

    private static final String POST_URL = "/reports/generate";
    private static final String GET_URL = "/reports/";
    private static final long REPORT_ID = 100L;

    private SessionFilter adminSessionFilter;
    private SessionFilter userSessionFilter;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        adminSessionFilter = new SessionFilter();
        userSessionFilter = new SessionFilter();

        when(reportService.generateReportAsync()).thenReturn(REPORT_ID);
        when(reportService.findById(REPORT_ID)).thenReturn(new Report());

        String adminCsrfToken = given()
                .filter(adminSessionFilter)
                .when()
                .get("/login")
                .htmlPath()
                .getString("html.body.form.input.find { it.@name == '_csrf' }.@value");

        given()
                .filter(adminSessionFilter)
                .param("username", "testName1")
                .param("password", "123")
                .param("_csrf", adminCsrfToken)
                .when()
                .post("/login")
                .then()
                .statusCode(302)
                .header("Location", endsWith("/passwords/view/entries"));

        String userCsrfToken = given()
                .filter(userSessionFilter)
                .when()
                .get("/login")
                .htmlPath()
                .getString("html.body.form.input.find { it.@name == '_csrf' }.@value");

        given()
                .filter(userSessionFilter)
                .param("username", "testName2")
                .param("password", "123")
                .param("_csrf", userCsrfToken)
                .when()
                .post("/login")
                .then()
                .statusCode(302)
                .header("Location", endsWith("/passwords/view/entries"));
    }

    @Test
    @DisplayName("Успешная генерация отчета администратором")
    void testGenerateReport_Success() {
        given()
                .filter(adminSessionFilter)
                .when()
                .post(POST_URL)
                .then()
                .statusCode(200)
                .body(equalTo("100"));
    }

    @Test
    @DisplayName("Отказ в доступе для пользователя без роли ADMIN")
    void testGenerateReport_ForbiddenForUser() {
        given()
                .filter(userSessionFilter)
                .when()
                .post(POST_URL)
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Неподдерживаемый метод")
    void testGenerateReport_MethodNotAllowed() {
        given()
                .filter(adminSessionFilter)
                .when()
                .get(POST_URL)
                .then()
                .statusCode(405);
    }

    @Test
    @DisplayName("Редирект на страницу входа для неавторизованного пользователя")
    void testGenerateReport_Unauthenticated() {
        given()
                .when()
                .post(POST_URL)
                .then()
                .statusCode(302);
    }

    @Test
    @DisplayName("Успешное получение отчета по Id")
    void testGetReportContent_Success() {
        given()
                .filter(adminSessionFilter)
                .when()
                .get(GET_URL + REPORT_ID)
                .then()
                .statusCode(200)
                .body(notNullValue());
    }

    @Test
    @DisplayName("Невалидный формате ID")
    void testGetReportContent_InvalidIdFormat() {
        given()
                .filter(adminSessionFilter)
                .when()
                .get(GET_URL + "test")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Ошибка при попытке получить несуществующий отчет")
    void getReportContent_NotFoundId() {
        when(reportService.findById(9999L)).thenThrow(new NotFoundReportException("Not found report"));

        given()
                .filter(adminSessionFilter)
                .when()
                .get(GET_URL + 9999L)
                .then()
                .statusCode(404);
    }
}
