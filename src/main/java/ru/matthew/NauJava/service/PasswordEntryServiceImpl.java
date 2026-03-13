package ru.matthew.NauJava.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.matthew.NauJava.config.Config;
import ru.matthew.NauJava.dao.PasswordEntryDaoImpl;
import ru.matthew.NauJava.entity.PasswordEntry;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PasswordEntryServiceImpl implements PasswordEntryService {

    private final Config config;
    private final PasswordEntryDaoImpl passwordEntryDao;

    private static final String DATA_FORMATTER = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final String APP_NAME_MSG = "%s  INFO Текущее название приложения: %s\n";
    private static final String VERSION_MSG = "%s  INFO Текущая версия приложения: %s\n";

    @Autowired
    public PasswordEntryServiceImpl(PasswordEntryDaoImpl passwordEntryDao, Config config) {
        this.passwordEntryDao = passwordEntryDao;
        this.config = config;
    }

    @PostConstruct
    public void init() {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATA_FORMATTER);
        String formattedDate = now.format(formatter);
        System.out.printf(APP_NAME_MSG, formattedDate, config.getAppName());
        System.out.printf(VERSION_MSG, formattedDate, config.getVersion());
    }

    @Override
    public void createPasswordEntry(Long id, String login, String password, String serviceName, String description) {
        var passwordEntry = new PasswordEntry();
        passwordEntry.setId(id);
        passwordEntry.setLogin(login);
        passwordEntry.setPassword(password);
        passwordEntry.setServiceName(serviceName);
        passwordEntry.setDescription(description);
        passwordEntryDao.save(passwordEntry);
    }

    @Override
    public PasswordEntry findById(Long id) {
        return passwordEntryDao.findById(id);
    }

    @Override
    public List<PasswordEntry> findByServiceName(String serviceName) {
        return passwordEntryDao.findByServiceName(serviceName);
    }

    @Override
    public List<PasswordEntry> findAll() {
        return passwordEntryDao.findAll();
    }

    @Override
    public void deleteById(Long id) {
        passwordEntryDao.deleteById(id);
    }

    @Override
    public void deleteByServiceName(String serviceName) {
        passwordEntryDao.deleteByServiceName(serviceName);
    }

    @Override
    public void updatePassword(Long id, String newPassword) {
        var passwordEntry = passwordEntryDao.findById(id);
        passwordEntry.setPassword(newPassword);
        passwordEntryDao.update(passwordEntry);
    }

    @Override
    public void updateLogin(Long id, String newLogin) {
        var passwordEntry = passwordEntryDao.findById(id);
        passwordEntry.setLogin(newLogin);
        passwordEntryDao.update(passwordEntry);
    }

    @Override
    public void updateServiceName(Long id, String newServiceName) {
        var passwordEntry = passwordEntryDao.findById(id);
        passwordEntry.setServiceName(newServiceName);
        passwordEntryDao.update(passwordEntry);
    }

    @Override
    public void updateDescription(Long id, String newDescription) {
        var passwordEntry = passwordEntryDao.findById(id);
        passwordEntry.setDescription(newDescription);
        passwordEntryDao.update(passwordEntry);
    }
}
