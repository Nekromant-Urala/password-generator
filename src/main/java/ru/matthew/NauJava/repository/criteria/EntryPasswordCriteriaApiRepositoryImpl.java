package ru.matthew.NauJava.repository.criteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.matthew.NauJava.entity.PasswordEntry;
import ru.matthew.NauJava.entity.ServiceEntry;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class EntryPasswordCriteriaApiRepositoryImpl implements EntryPasswordCriteriaApiRepository {

    private final EntityManager entityManager;

    @Autowired
    public EntryPasswordCriteriaApiRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<PasswordEntry> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Некорректные значения временного диапазона!");
        }
        var builder = entityManager.getCriteriaBuilder();
        var criteriaQuery = builder.createQuery(PasswordEntry.class);

        var root = criteriaQuery.from(PasswordEntry.class);

        var predicate = builder.between(root.get("createdAt"), startDate, endDate);

        criteriaQuery.select(root).where(predicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<PasswordEntry> findByServiceName(String serviceName) {
        if (serviceName == null || serviceName.trim().isEmpty()) {
            throw new IllegalArgumentException("Некорректное название сервиса!");
        }

        var builder = entityManager.getCriteriaBuilder();
        var criteriaQuery = builder.createQuery(PasswordEntry.class);
        var root = criteriaQuery.from(PasswordEntry.class);

        Join<PasswordEntry, ServiceEntry> join = root.join("serviceName", JoinType.INNER);
        var predicate = builder.equal(join.get("name"), serviceName);

        criteriaQuery.select(root).where(predicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
