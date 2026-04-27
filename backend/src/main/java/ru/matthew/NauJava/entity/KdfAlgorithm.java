package ru.matthew.NauJava.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "kdf_algorithms")
public class KdfAlgorithm {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "default_iterations")
    private Integer defaultIterations;

    @Column(name = "min_iterations")
    private Integer minIterations;

    @Column(name = "max_iterations")
    private Integer maxIterations;

    @Column(name = "memory_cost")
    private Integer memoryCost;

    @Column(name = "parallelism")
    private Integer parallelism;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDefaultIterations() {
        return defaultIterations;
    }

    public void setDefaultIterations(Integer defaultIterations) {
        this.defaultIterations = defaultIterations;
    }

    public Integer getMinIterations() {
        return minIterations;
    }

    public void setMinIterations(Integer minIterations) {
        this.minIterations = minIterations;
    }

    public Integer getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(Integer maxIterations) {
        this.maxIterations = maxIterations;
    }

    public Integer getMemoryCost() {
        return memoryCost;
    }

    public void setMemoryCost(Integer memoryCost) {
        this.memoryCost = memoryCost;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }
}
