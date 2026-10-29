package com.example.EventYunks.repository;

import com.example.EventYunks.model.Recinto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de recintos. Las consultas de lista traen la dirección y las zonas en la misma consulta (EntityGraph)
 * para evitar consultas extra por cada tarjeta de la lista
 */
@Repository
public interface RecintoRepository extends JpaRepository<Recinto, Long> {

    /**
     * Regresa todos los recintos ordenados alfabéticamente por nombre
     * @return una lista de recintos con su dirección y zonas
     */
    @EntityGraph(attributePaths = {"direccion", "zonas"})
    List<Recinto> findAllByOrderByNombreAsc();

    /**
     * Consulta que busca recintos según su nombre o su ciudad (sin distinguir mayúsculas)
     * @param palabra el texto a buscar
     * @return una lista de recintos que coincidan, ordenados por nombre
     */
    @EntityGraph(attributePaths = {"direccion", "zonas"})
    @Query("SELECT r FROM Recinto r WHERE LOWER(r.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) " +
            "OR LOWER(r.direccion.ciudad) LIKE LOWER(CONCAT('%', :palabra, '%')) ORDER BY r.nombre")
    List<Recinto> buscarPorNombreOCiudad(@Param("palabra") String palabra);

    /**
     * Revisa si existe un recinto con ese nombre en esa ciudad (sin distinguir mayúsculas). Se usa al crear
     * @param nombre el nombre del recinto
     * @param ciudad la ciudad del recinto
     * @return true si ya existe, false en caso contrario
     */
    boolean existsByNombreIgnoreCaseAndDireccion_CiudadIgnoreCase(String nombre, String ciudad);

    /**
     * Igual que el anterior pero excluyendo un recinto por su id. Se usa al editar para no compararlo consigo mismo
     * @param nombre el nombre del recinto
     * @param ciudad la ciudad del recinto
     * @param id el id del recinto a excluir
     * @return true si ya existe otro recinto igual, false en caso contrario
     */
    boolean existsByNombreIgnoreCaseAndDireccion_CiudadIgnoreCaseAndIdNot(String nombre, String ciudad, Long id);
}
