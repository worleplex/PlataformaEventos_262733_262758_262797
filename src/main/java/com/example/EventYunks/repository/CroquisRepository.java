package com.example.EventYunks.repository;

import com.example.EventYunks.model.Croquis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CroquisRepository extends JpaRepository<Croquis, Long> {
    List<Croquis> findAllByOrderByNombreAsc();
}
