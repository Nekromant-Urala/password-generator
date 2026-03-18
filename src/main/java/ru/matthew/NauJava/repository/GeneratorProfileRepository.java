package ru.matthew.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.matthew.NauJava.entity.GeneratorProfile;


public interface GeneratorProfileRepository extends JpaRepository<GeneratorProfile, Long> {
}
