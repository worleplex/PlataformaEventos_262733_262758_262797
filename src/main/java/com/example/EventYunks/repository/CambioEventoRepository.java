package com.example.EventYunks.repository;

import com.example.EventYunks.model.CambioEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CambioEventoRepository extends JpaRepository<CambioEvento, Long> {

}
