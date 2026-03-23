package ru.matthew.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.matthew.NauJava.entity.GeneratorProfile;

import java.util.List;


public interface GeneratorProfileRepository extends JpaRepository<GeneratorProfile, Long> {
}
