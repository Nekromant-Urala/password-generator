package ru.matthew.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.matthew.NauJava.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {
}
