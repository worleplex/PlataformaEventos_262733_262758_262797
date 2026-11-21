package com.example.EventYunks.repository;

import com.example.EventYunks.model.Evento;
import com.example.EventYunks.model.enums.EstadoEvento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    @EntityGraph(attributePaths = {"recinto", "organizador", "boletos"})
    List<Evento> findByEstadoNot(EstadoEvento estado);

    @Query("SELECT DISTINCT e.categoria FROM Evento e WHERE e.categoria IS NOT NULL AND e.categoria <> '' "
            + "AND e.estado <> :estado ORDER BY e.categoria")
    List<String> categoriasDeEventosExcepto(@Param("estado") EstadoEvento estado);

    long countByOrganizador_IdAndEstado(Long organizadorId, EstadoEvento estado);

    @EntityGraph(attributePaths = {"recinto", "recinto.croquis", "boletos"})
    List<Evento> findByOrganizador_IdOrderByFechaDesc(Long organizadorId);

    long countByEstado(EstadoEvento estado);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Evento e WHERE e.recinto.id = :recintoId AND e.fecha = :fecha AND e.estado IN :estados AND (:eventoId IS NULL OR e.id <> :eventoId)")
    boolean existeEmpalmeEventos(@Param("recintoId") Long recintoId, @Param("fecha") LocalDateTime fecha, @Param("estados") List<EstadoEvento> estados, @Param("eventoId") Long eventoId);
}
