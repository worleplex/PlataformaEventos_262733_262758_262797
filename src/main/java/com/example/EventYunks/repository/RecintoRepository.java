package com.example.EventYunks.repository;

import com.example.EventYunks.model.Recinto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecintoRepository extends JpaRepository<Recinto, Long> {

    @EntityGraph(attributePaths = {"direccion", "croquis", "croquis.zonas"})
    List<Recinto> findAllByOrderByNombreAsc();

    @EntityGraph(attributePaths = {"direccion", "croquis", "croquis.zonas"})
    @Query("SELECT r FROM Recinto r WHERE LOWER(r.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) " +
            "OR LOWER(r.direccion.ciudad) LIKE LOWER(CONCAT('%', :palabra, '%')) ORDER BY r.nombre")
    List<Recinto> buscarPorNombreOCiudad(@Param("palabra") String palabra);

    boolean existsByNombreIgnoreCaseAndDireccion_CiudadIgnoreCase(String nombre, String ciudad);

    boolean existsByNombreIgnoreCaseAndDireccion_CiudadIgnoreCaseAndIdNot(String nombre, String ciudad, Long id);
}
