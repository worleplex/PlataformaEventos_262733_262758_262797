package com.example.EventYunks.service;

import com.example.EventYunks.model.Direccion;
import com.example.EventYunks.model.Recinto;
import com.example.EventYunks.repository.RecintoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
public class RecintoService {

    private final RecintoRepository recintoRepository;
    private final AdministradorService administradorService;

    /**
     * constructor con inyeccion de dependencias
     * @param recintoRepository el repositorio de recintos
     * @param administradorService el servicio que da el administrador de la sesión activa
     */
    public RecintoService(RecintoRepository recintoRepository, AdministradorService administradorService) {
        this.recintoRepository = recintoRepository;
        this.administradorService = administradorService;
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
     * Método que guarda un recinto nuevo en la BD y le asigna el administrador de la sesión activa
     * @param recinto el recinto (con su dirección) capturado en el formulario
     * @return el recinto ya persistido
     */
    public Recinto crear(Recinto recinto) {
        recinto.setId(null);
        limpiarDatos(recinto);
        recinto.setAdministrador(administradorService.obtenerAdministradorActual());
        return recintoRepository.save(recinto);
    }

    /**
     * Método que actualiza un recinto existente con los datos capturados en el formulario. Solo se copian
     * los datos del formulario (nombre, capacidad y dirección) para no perder zonas, croquis ni administrador
     * @param id el id del recinto a actualizar
     * @param datos el recinto (con su dirección) capturado en el formulario
     * @return el recinto ya persistido
     * @throws NoSuchElementException si se intenta editar un recinto que ya no existe
     */
    public Recinto actualizar(Long id, Recinto datos) {
        Recinto recinto = recintoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el recinto con id " + id));
        if (recinto.getDireccion() == null) {
            recinto.setDireccion(new Direccion());
        }
        limpiarDatos(datos);

        recinto.setNombre(datos.getNombre());
        recinto.setCapacidad(datos.getCapacidad());

        Direccion direccion = recinto.getDireccion();
        Direccion nueva = datos.getDireccion();
        direccion.setNombreLugar(nueva.getNombreLugar());
        direccion.setCalle(nueva.getCalle());
        direccion.setNumero(nueva.getNumero());
        direccion.setColonia(nueva.getColonia());
        direccion.setCodigoPostal(nueva.getCodigoPostal());
        direccion.setCiudad(nueva.getCiudad());

        return recintoRepository.save(recinto);
    }

    /**
     * Método que revisa si ya existe otro recinto con el mismo nombre en la misma ciudad
     * Al editar (cuando el recinto trae id) se excluye el propio recinto de la comparación
     * @param recinto el recinto capturado en el formulario
     * @return true si ya existe un recinto igual, false en caso contrario
     */
    public boolean existeDuplicado(Recinto recinto) {
        if (recinto.getNombre() == null || recinto.getDireccion() == null || recinto.getDireccion().getCiudad() == null) {
            return false;
        }
        String nombre = recinto.getNombre().trim();
        String ciudad = recinto.getDireccion().getCiudad().trim();
        if (recinto.getId() == null) {
            return recintoRepository.existsByNombreIgnoreCaseAndDireccion_CiudadIgnoreCase(nombre, ciudad);
        }
        return recintoRepository.existsByNombreIgnoreCaseAndDireccion_CiudadIgnoreCaseAndIdNot(nombre, ciudad, recinto.getId());
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
     * Método que quita los espacios en los extremos de los textos capturados en el formulario
     * @param recinto el recinto (con su dirección) a limpiar
     */
    private void limpiarDatos(Recinto recinto) {
        recinto.setNombre(recinto.getNombre().trim());
        Direccion d = recinto.getDireccion();
        d.setNombreLugar(limpiarOpcional(d.getNombreLugar()));
        d.setCalle(d.getCalle().trim());
        d.setNumero(d.getNumero().trim());
        d.setColonia(d.getColonia().trim());
        d.setCodigoPostal(d.getCodigoPostal().trim());
        d.setCiudad(d.getCiudad().trim());
    }

    /**
     * Método que convierte los textos vacíos en null para no guardar cadenas en blanco en campos opcionales
     * @param valor el texto a limpiar
     * @return el texto sin espacios en los extremos, o null si venía vacío
     */
    private String limpiarOpcional(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}
