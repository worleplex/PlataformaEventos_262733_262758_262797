package com.example.EventYunks.repository;

import com.example.EventYunks.model.Evento;
import com.example.EventYunks.model.enums.EstadoEvento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    @EntityGraph(attributePaths = {"recinto", "organizador", "boletos"})
    List<Evento> findByEstadoNot(EstadoEvento estado);

    @Query("SELECT DISTINCT e.categoria FROM Evento e WHERE e.categoria IS NOT NULL AND e.categoria <> '' "
            + "AND e.estado <> :estado ORDER BY e.categoria")
    List<String> categoriasDeEventosExcepto(@Param("estado") EstadoEvento estado);

    long countByOrganizador_IdAndEstado(Long organizadorId, EstadoEvento estado);
}
