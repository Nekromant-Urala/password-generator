package ru.matthew.NauJava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.matthew.NauJava.entity.PasswordEntry;
import ru.matthew.NauJava.entity.ServiceEntry;
import ru.matthew.NauJava.entity.User;
import ru.matthew.NauJava.exception.PasswordEntryNotFoundException;
import ru.matthew.NauJava.exception.ServiceDataNotFoundException;
import ru.matthew.NauJava.repository.PasswordEntryRepository;
import ru.matthew.NauJava.repository.ServiceEntryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PasswordEntryServiceImpl implements PasswordEntryService {

    private final PasswordEntryRepository passwordEntryRepository;
    private final ServiceEntryRepository serviceEntryRepository;


    @Autowired
    public PasswordEntryServiceImpl(PasswordEntryRepository passwordEntryRepository, ServiceEntryRepository serviceEntryRepository) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.serviceEntryRepository = serviceEntryRepository;
    }


    @Override
    @Transactional
    public void createPasswordEntry(String login, String password, String description, ServiceEntry serviceName, User user) {
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
        var serviceEntry = serviceEntryRepository.findServiceEntryByName(serviceName).orElseThrow(
                () -> new ServiceDataNotFoundException("Сервис с таким именем не удалось найти")
        );
        passwordEntryRepository.deletePasswordEntriesByServiceName(serviceEntry);
    }

    @Override
    @Transactional
    public void updatePassword(Long id, String newPassword) {
        var passwordEntry = passwordEntryRepository.findById(id).orElseThrow(
                () -> new PasswordEntryNotFoundException("Не удалось найти запись с таким id: " + id)
        );
        passwordEntry.setPassword(newPassword);
        passwordEntryRepository.save(passwordEntry);
    }

    @Override
    @Transactional
    public void updateLogin(Long id, String newLogin) {
        var passwordEntry = passwordEntryRepository.findById(id).orElseThrow(
                () -> new PasswordEntryNotFoundException("Не удалось найти запись с таким id: " + id)
        );
        passwordEntry.setLogin(newLogin);
        passwordEntryRepository.save(passwordEntry);
    }

    @Override
    @Transactional
    public void updateServiceName(Long id, String newServiceName) {
        var passwordEntry = passwordEntryRepository.findById(id).orElseThrow(
                () -> new PasswordEntryNotFoundException("Не удалось найти запись с таким id: " + id)
        );
        var newServiceEntry = serviceEntryRepository.findServiceEntryByName(newServiceName).orElseThrow(
                () -> new ServiceDataNotFoundException("Сервис с таким именем не удалось найти")
        );
        passwordEntry.setServiceName(newServiceEntry);
        passwordEntryRepository.save(passwordEntry);
    }

    @Override
    @Transactional
    public void updateDescription(Long id, String newDescription) {
        var passwordEntry = passwordEntryRepository.findById(id).orElseThrow(
                () -> new PasswordEntryNotFoundException("Не удалось найти запись с таким id: " + id)
        );
        passwordEntry.setDescription(newDescription);
        passwordEntryRepository.save(passwordEntry);
    }
}