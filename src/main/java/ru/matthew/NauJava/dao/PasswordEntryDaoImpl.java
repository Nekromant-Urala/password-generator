package ru.matthew.NauJava.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.matthew.NauJava.entity.PasswordEntry;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Repository
public class PasswordEntryDaoImpl implements PasswordEntryDao<PasswordEntry, Long> {

    private final HashMap<Long, PasswordEntry> passwordEntries;

    private static final String ENTITY_EXISTS = "запись уже с таким ID существует";

    @Autowired
    public PasswordEntryDaoImpl(HashMap<Long, PasswordEntry> passwordEntries) {
        this.passwordEntries = passwordEntries;
    }

    @Override
    public void save(PasswordEntry entity) {
        var previous = passwordEntries.putIfAbsent(entity.getId(), entity);
        if (previous != null) {
            throw new IllegalStateException(ENTITY_EXISTS);
        }
    }

    @Override
    public PasswordEntry findById(Long id) {
        return passwordEntries.getOrDefault(id, null);
    }

    @Override
    public List<PasswordEntry> findByServiceName(String serviceName) {
        return passwordEntries.values()
                .stream()
                .filter(e -> Objects.equals(e.getServiceName(), serviceName))
                .toList();
    }

    @Override
    public List<PasswordEntry> findAll() {
        return passwordEntries.values()
                .stream()
                .map(PasswordEntry::new)
                .toList();
    }

    @Override
    public void update(PasswordEntry entity) {
        entity.setUpdated_at(LocalDateTime.now());
        passwordEntries.put(entity.getId(), entity);
    }

    @Override
    public void deleteById(Long id) {
        passwordEntries.remove(id);
    }

    @Override
    public void deleteByServiceName(String serviceName) {
        passwordEntries.values()
                .removeIf(entry -> Objects.equals(entry.getServiceName(), serviceName));
    }
}
