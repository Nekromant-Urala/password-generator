package ru.matthew.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.matthew.NauJava.entity.KdfAlgorithm;

public interface KdfAlgorithmRepository extends JpaRepository<KdfAlgorithm, Long> {
}
