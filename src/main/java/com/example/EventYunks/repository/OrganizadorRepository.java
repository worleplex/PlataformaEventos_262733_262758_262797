package com.example.EventYunks.repository;

import com.example.EventYunks.model.Organizador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizadorRepository extends JpaRepository<Organizador, Long> {

    List<Organizador> findAllByOrderByNombreAsc();

    @Query("SELECT o FROM Organizador o WHERE LOWER(o.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR LOWER(o.correo) LIKE LOWER(CONCAT('%', :palabra, '%')) ORDER BY o.nombre")
    List<Organizador> buscarPorNombreOCorreo(@Param("palabra") String palabra);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCaseAndIdNot(String correo, Long id);
}
