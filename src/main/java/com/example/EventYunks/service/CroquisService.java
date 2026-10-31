package com.example.EventYunks.service;

import com.example.EventYunks.model.Croquis;
import com.example.EventYunks.repository.CroquisRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CroquisService {

    private final CroquisRepository croquisRepository;

    /**
     * constructor con inyeccion de dependencias
     * @param croquisRepository el repositorio de croquis
     */
    public CroquisService(CroquisRepository croquisRepository) {
        this.croquisRepository = croquisRepository;
    }

    /**
     * Método que regresa todos los croquis ordenados por nombre
     * @return una lista de croquis
     */
    public List<Croquis> listarTodos() {
        return croquisRepository.findAllByOrderByNombreAsc();
    }

    /**
     * Método que obtiene un croquis por su id
     * @param id el id del croquis a obtener
     * @return un Optional con el croquis si lo encontró, vacío en caso contrario
     */
    public Optional<Croquis> buscarPorId(Long id) {
        return croquisRepository.findById(id);
    }
}
