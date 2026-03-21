package ru.matthew.NauJava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.matthew.NauJava.entity.PasswordEntry;
import ru.matthew.NauJava.entity.User;
import ru.matthew.NauJava.exception.PasswordEntryNotFoundException;
import ru.matthew.NauJava.repository.PasswordEntryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PasswordEntryServiceImpl implements PasswordEntryService {

    private final PasswordEntryRepository passwordEntryRepository;

    private static final String NOT_FOUND_ENTRY = "Не удалось найти запись с таким id: ";

    @Autowired
    public PasswordEntryServiceImpl(PasswordEntryRepository passwordEntryRepository) {
        this.passwordEntryRepository = passwordEntryRepository;
    }


    @Override
    @Transactional
    public void createPasswordEntry(String login, String password, String description, String serviceName, User user) {
        var passwordEntry = new PasswordEntry();
        passwordEntry.setLogin(login);
        passwordEntry.setPassword(password);
        passwordEntry.setDescription(description);
        passwordEntry.setServiceName(serviceName);
        passwordEntry.setUser(user);

        passwordEntryRepository.save(passwordEntry);
    }

    @Override
    public Optional<PasswordEntry> findById(Long id) {
        return passwordEntryRepository.findById(id);
    }

    @Override
    public List<PasswordEntry> findByServiceName(String serviceName) {
        return passwordEntryRepository.findByServiceName(serviceName);
    }

    @Override
    public List<PasswordEntry> findAll() {
        return passwordEntryRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        passwordEntryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByServiceName(String serviceName) {
        passwordEntryRepository.deleteByServiceName(serviceName);
    }

    @Override
    @Transactional
    public void updatePassword(Long id, String newPassword) {
        var passwordEntry = passwordEntryRepository.findById(id).orElseThrow(
                () -> new PasswordEntryNotFoundException(NOT_FOUND_ENTRY + id)
        );
        passwordEntry.setPassword(newPassword);
        passwordEntryRepository.save(passwordEntry);
    }

    @Override
    @Transactional
    public void updateLogin(Long id, String newLogin) {
        var passwordEntry = passwordEntryRepository.findById(id).orElseThrow(
                () -> new PasswordEntryNotFoundException(NOT_FOUND_ENTRY + id)
        );
        passwordEntry.setLogin(newLogin);
        passwordEntryRepository.save(passwordEntry);
    }

    @Override
    @Transactional
    public void updateServiceName(Long id, String newServiceName) {
        var passwordEntry = passwordEntryRepository.findById(id).orElseThrow(
                () -> new PasswordEntryNotFoundException(NOT_FOUND_ENTRY + id)
        );
        passwordEntry.setServiceName(newServiceName);
        passwordEntryRepository.save(passwordEntry);
    }

    @Override
    @Transactional
    public void updateDescription(Long id, String newDescription) {
        var passwordEntry = passwordEntryRepository.findById(id).orElseThrow(
                () -> new PasswordEntryNotFoundException(NOT_FOUND_ENTRY + id)
        );
        passwordEntry.setDescription(newDescription);
        passwordEntryRepository.save(passwordEntry);
    }
}