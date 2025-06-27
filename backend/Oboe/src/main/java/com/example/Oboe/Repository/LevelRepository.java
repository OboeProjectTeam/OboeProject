package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LevelRepository extends JpaRepository<Level, UUID> {
}

