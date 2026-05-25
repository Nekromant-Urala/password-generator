package ru.matthew.NauJava.domain.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ru.matthew.NauJava.domain.audit.dto.AuditCreateDto;
import ru.matthew.NauJava.domain.audit.dto.AuditEventDto;
import ru.matthew.NauJava.domain.user.UserService;

import static ru.matthew.NauJava.domain.audit.EventType.LOGOUT_USER;
import static ru.matthew.NauJava.domain.audit.EventType.SIGN_IN_USER;

@Component
public class AuditEventListener {

    private final AuditService auditService;
    private final UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditEventListener.class);

    @Autowired
    public AuditEventListener(AuditService auditService, UserService userService) {
        this.auditService = auditService;
        this.userService = userService;
    }

    @EventListener
    public void onAuditEvent(AuditEventDto event) {
        auditService.createEvent(new AuditCreateDto(
                event.userId(),
                event.eventType(),
                getCurrentUserAgent(),
                event.description()
        ));
    }

    @EventListener
    public void onLoginSuccess(AuthenticationSuccessEvent event) {
        var authentication = event.getAuthentication();
        String username = authentication.getName();

        writeLog(username, SIGN_IN_USER, "вошел в систему");
    }

    @EventListener
    public void onLogoutSuccess(LogoutSuccessEvent event) {
        var authentication = event.getAuthentication();
        String username = authentication.getName();

        writeLog(username, LOGOUT_USER, "вышел из системы");
    }

    private void writeLog(String username, EventType event, String description) {
        var user = userService.findByUsername(username);
        if (user != null) {
            var userAgent = getCurrentUserAgent();
            auditService.createEvent(new AuditCreateDto(
                    user.id(),
                    event,
                    userAgent,
                    description
            ));
            LOGGER.info("Пользователь с id:{} {}. С клиента: {}", user.id(), description, userAgent);
        }
    }

    private String getCurrentUserAgent() {
        String userAgent = "Unknown";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String header = request.getHeader("User-Agent");
            if (header != null) {
                userAgent = header;
            }
        }
        return userAgent;
    }
}
