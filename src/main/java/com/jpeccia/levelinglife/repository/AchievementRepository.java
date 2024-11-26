package com.jpeccia.levelinglife.repository;

import com.jpeccia.levelinglife.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    Optional<Achievement> findByName(String name);  // Método para buscar uma conquista por nome
}
