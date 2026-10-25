package com.example.EventYunks.repository;

import com.example.EventYunks.model.Artista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistaRepository extends JpaRepository<Artista, Long> {

    List<Artista> findAllByOrderByNombreAsc();

    //Consulta que busca artistas según su nombre o su género musical
    @Query("SELECT a FROM Artista a WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :palabra, '%')) OR LOWER(a.generoMusical) LIKE LOWER(CONCAT('%', :palabra, '%')) ORDER BY a.nombre")
    List<Artista> buscarPorNombreOGenero(@Param("palabra") String palabra);
}
