package com.example.EventYunks.service;

import com.example.EventYunks.dto.RecintoForm;
import com.example.EventYunks.model.Administrador;
import com.example.EventYunks.model.Direccion;
import com.example.EventYunks.model.Recinto;
import com.example.EventYunks.repository.AdministradorRepository;
import com.example.EventYunks.repository.RecintoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
public class RecintoService {

    private final RecintoRepository recintoRepository;
    private final AdministradorRepository administradorRepository;

    /**
     * constructor con inyeccion de dependencias
     * @param recintoRepository el repositorio de recintos
     * @param administradorRepository el repositorio de administradores
     */
    public RecintoService(RecintoRepository recintoRepository, AdministradorRepository administradorRepository) {
        this.recintoRepository = recintoRepository;
        this.administradorRepository = administradorRepository;
    }

    /**
     * Método que regresa todos los recintos de la base de datos ordenados por nombre
     * @return una lista de recintos
     */
    public List<Recinto> listarTodos() {
        return recintoRepository.findAllByOrderByNombreAsc();
    }

    /**
     * Este método se usa para la consulta de la barra de búsqueda, si el parámetro
     * viene vacío, regresa todos los recintos, y si viene con algo manda a llamar
     * al método buscarPorNombreOCiudad
     * @param texto el texto a filtrar
     * @return una lista de recintos que coincidan con los filtros de búsqueda
     */
    public List<Recinto> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return listarTodos();
        }
        return recintoRepository.buscarPorNombreOCiudad(texto.trim());
    }

    /**
     * Método que obtiene un recinto por su id
     * @param id el id del recinto a obtener
     * @return un Optional con el recinto si lo encontró, vacío en caso contrario
     */
    public Optional<Recinto> buscarPorId(Long id) {
        return recintoRepository.findById(id);
    }

    /**
     * Método que cuenta cuántos recintos hay registrados en total
     * @return el total de recintos
     */
    public long contarTodos() {
        return recintoRepository.count();
    }

    /**
     * Método que guarda un recinto en la BD y si el formulario no trae id crea uno nuevo
     * si trae id actualiza el recinto existente junto con su dirección
     * @param form los datos del recinto capturados en el formulario
     * @return el recinto ya persistido
     * @throws NoSuchElementException si se intenta editar un recinto que ya no existe, va a hacer cambiado estop
     */
    public Recinto guardar(RecintoForm form) {
        Recinto recinto;
        if (form.getId() == null) {
            recinto = new Recinto();
            recinto.setDireccion(new Direccion());
            recinto.setAdministrador(obtenerAdministradorActual());
        } else {
            recinto = recintoRepository.findById(form.getId())
                    .orElseThrow(() -> new NoSuchElementException("No existe el recinto con id " + form.getId()));
            if (recinto.getDireccion() == null) {
                recinto.setDireccion(new Direccion());
            }
        }

        recinto.setNombre(form.getNombre().trim());
        recinto.setCapacidad(form.getCapacidad());

        Direccion direccion = recinto.getDireccion();
        direccion.setNombreLugar(limpiarOpcional(form.getNombreLugar()));
        direccion.setCalle(form.getCalle().trim());
        direccion.setNumero(form.getNumero().trim());
        direccion.setColonia(form.getColonia().trim());
        direccion.setCodigoPostal(form.getCodigoPostal().trim());
        direccion.setCiudad(form.getCiudad().trim());

        return recintoRepository.save(recinto);
    }

    /**
     * Método que revisa si ya existe otro recinto con el mismo nombre en la misma ciudad
     * Al editar se excluye el propio recinto de la comparación
     * @param form los datos del recinto capturados en el formulario
     * @return true si ya existe un recinto igual, false en caso contrario
     */
    public boolean existeDuplicado(RecintoForm form) {
        if (form.getNombre() == null || form.getCiudad() == null) {
            return false;
        }
        String nombre = form.getNombre().trim();
        String ciudad = form.getCiudad().trim();
        if (form.getId() == null) {
            return recintoRepository.existsByNombreIgnoreCaseAndDireccion_CiudadIgnoreCase(nombre, ciudad);
        }
        return recintoRepository.existsByNombreIgnoreCaseAndDireccion_CiudadIgnoreCaseAndIdNot(nombre, ciudad, form.getId());
    }

    /**
     * Método que elimina un recinto de la BD
     * @param id el id del recinto a eliminar
     * @return true si se eliminó, false si no existe o tiene eventos asociados
     */
    public boolean eliminar(Long id) {
        Optional<Recinto> recinto = recintoRepository.findById(id);
        if (recinto.isEmpty() || !recinto.get().getEventos().isEmpty()) {
            return false;
        }
        recintoRepository.delete(recinto.get());
        recintoRepository.flush();
        return true;
    }

    /**
     * Método que convierte los textos vacíos en null para no guardar cadenas en blanco en campos opcionales
     * @param valor el texto a limpiar
     * @return el texto sin espacios en los extremos, o null si venía vacío
     */
    private String limpiarOpcional(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }

    /**
     * Método que obtiene el administrador que se le asigna a los recintos nuevos eh por ahora el primero de la BD
     * aún no existe manejo de sesiones tolano, pero ahorita queda
     * @return el administrador actual
     */
    private Administrador obtenerAdministradorActual() {
        return administradorRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new IllegalStateException("No hay ningún administrador en la base de datos."));
    }
}
