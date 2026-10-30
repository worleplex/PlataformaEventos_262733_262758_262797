package com.example.EventYunks.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.EventYunks.model.Artista;
import com.example.EventYunks.repository.ArtistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class ArtistaService {

    @Autowired
    private ArtistaRepository artistaRepository;

    @Autowired
    private AdministradorService administradorService;

    @Autowired(required = false)
    private Cloudinary cloudinary;

    /**
     * Método que regresa todos los artistas de la base de datos
     * @return una lista de artistas
     */
    public List<Artista> listarTodos() {
        return artistaRepository.findAllByOrderByNombreAsc();
    }

    /**
     * Este método se usa para la consulta de la barra de búsqueda, si el parámetro viene vacío, regresa todos los artistas,
     * si viene con algo manda a llamar al método buscarPorNombreOGenero
     * @param texto el texto a filtrar
     * @return una lista de artistas que coincidan con los filtros de búsqueda
     */
    public List<Artista> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }
        return artistaRepository.buscarPorNombreOGenero(texto.trim());
    }

    /**
     * Método que obtiene un artista por su id
     * @param id el id del artista a obtener
     * @return el artista si lo encontró, null en caso contrario
     */
    public Artista obtenerArtistaPorId(Long id) {
        return artistaRepository.findById(id).orElse(null);
    }

    /**
     * Método que guarda un artista en la base de datos junto a su imagen
     * @param artista el artista a persistir
     * @param imagen la imagen del artista a persistir
     */
    public void guardarArtista(Artista artista, MultipartFile imagen) {
        String urlImagen = artista.getUrlImagen();
        if (imagen != null && !imagen.isEmpty()) {
            String nueva = guardarImagenCloudinary(imagen);
            if (nueva != null) {
                urlImagen = nueva;
            }
        }
        artista.setUrlImagen(urlImagen);
        // Si es un artista nuevo se registra con el administrador de la sesión activa
        if (artista.getAdministrador() == null) {
            artista.setAdministrador(administradorService.obtenerAdministradorActual());
        }
        artistaRepository.save(artista);
    }

    /**
     * Método que elimina un artista de la base de datos (si este no está asociado a ningún evento)
     * @param id el id del artista a eliminar
     */
    public boolean eliminarArtista(Long id) {
        Artista artista = artistaRepository.findById(id).orElse(null);
        if (artista != null) {
            // Me imagino que no puedes borrar un artista que ya forme parte de algún evento, por eso de las llaves foraneas,
            //aunque tal vez podría hacer que se borrara el artista especifico de la lista de artistas del evento, hmmm
            if (!artista.getEventos().isEmpty()) {
                return false;
            }
            artistaRepository.deleteById(id);
        }
        return true;
    }

    /**
     * Método que guarda las imagenes en cloudinary
     * @param imagen la imagen a guardar
     * @return una cadena de texto con el resultado de la subida
     */
    private String guardarImagenCloudinary(MultipartFile imagen) {
        try {
            Map uploadResult = cloudinary.uploader().upload(imagen.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            System.err.println("Error al subir a Cloudinary. Revisa tus credenciales.");
            e.printStackTrace();
            return null;
        }
    }
}
