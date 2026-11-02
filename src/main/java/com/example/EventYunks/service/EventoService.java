package com.example.EventYunks.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.EventYunks.dto.BoletoBorradorDTO;
import com.example.EventYunks.dto.EventoBorradorDTO;
import com.example.EventYunks.model.*;
import com.example.EventYunks.model.enums.EstadoEvento;
import com.example.EventYunks.repository.ArtistaRepository;
import com.example.EventYunks.repository.CambioEventoRepository;
import com.example.EventYunks.repository.EventoRepository;
import com.example.EventYunks.repository.RecintoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventoService {

    private static final BigDecimal LIMITE_BAJO = new BigDecimal("500");
    private static final BigDecimal LIMITE_MEDIO = new BigDecimal("1000");

    private final EventoRepository eventoRepository;
    private final RecintoRepository recintoRepository;
    private final ArtistaRepository artistaRepository;
    private final CambioEventoRepository cambioEventoRepository;

    @Autowired(required = false)
    private Cloudinary cloudinary;

    public EventoService(EventoRepository eventoRepository, RecintoRepository recintoRepository, ArtistaRepository artistaRepository, CambioEventoRepository cambioEventoRepository) {
        this.eventoRepository = eventoRepository;
        this.recintoRepository = recintoRepository;
        this.artistaRepository = artistaRepository;
        this.cambioEventoRepository = cambioEventoRepository;
    }

    /**
     * Catálogo de eventos del administrador: los próximos primero (del más cercano al más lejano)
     * y después los ya pasados (del más reciente al más antiguo). No incluye borradores
     * @return la lista de eventos que cumplen los filtros
     */
    @Transactional(readOnly = true)
    public List<Evento> catalogoAdmin(String buscar, String categoria, String fecha, String precio) {
        LocalDateTime ahora = LocalDateTime.now();
        String texto = buscar == null ? "" : buscar.trim().toLowerCase(Locale.ROOT);

        List<Evento> filtrados = eventoRepository.findByEstadoNot(EstadoEvento.BORRADOR).stream()
                .filter(e -> coincideTexto(e, texto))
                .filter(e -> categoria == null || categoria.isBlank() || categoria.equalsIgnoreCase(e.getCategoria()))
                .filter(e -> coincideFecha(e.getFecha(), fecha, ahora))
                .filter(e -> coincidePrecio(e.getPrecioDesde(), precio))
                .collect(Collectors.toList());

        Comparator<Evento> porFecha = Comparator.comparing(Evento::getFecha);
        List<Evento> ordenados = new ArrayList<>(filtrados.stream()
                .filter(e -> !e.getFecha().isBefore(ahora))
                .sorted(porFecha)
                .toList());
        ordenados.addAll(filtrados.stream()
                .filter(e -> e.getFecha().isBefore(ahora))
                .sorted(porFecha.reversed())
                .toList());
        return ordenados;
    }

    @Transactional(readOnly = true)
    public List<String> categorias() {
        return eventoRepository.categoriasDeEventosExcepto(EstadoEvento.BORRADOR);
    }

    public List<Evento> eventosDelOrganizador(Long organizadorId) {
        return eventoRepository.findByOrganizador_IdOrderByFechaDesc(organizadorId);
    }

    @Transactional(readOnly = true)
    public List<String> validarBorrador(EventoBorradorDTO borrador, boolean publicar) {
        List<String> errores = new ArrayList<>();
        if (borrador.getNombre() == null) {
            errores.add("El nombre del evento es obligatorio.");
        }
        if (borrador.getFecha() == null || borrador.getHora() == null) {
            errores.add("La fecha y la hora del evento son obligatorias.");
        } else if (publicar && !LocalDateTime.of(borrador.getFecha(), borrador.getHora()).isAfter(LocalDateTime.now())) {
            errores.add("Para publicar el evento, su fecha debe ser futura.");
        }
        Recinto recinto = borrador.getRecintoId() == null ? null : recintoRepository.findById(borrador.getRecintoId()).orElse(null);
        if (recinto == null) {
            errores.add("Selecciona un recinto para el evento.");
        }
        int capturados = 0;
        int totalLugares = 0;
        List<BoletoBorradorDTO> boletos = borrador.getBoletos();
        for (int i = 0; i < boletos.size(); i++) {
            BoletoBorradorDTO b = boletos.get(i);
            if (!tieneDatos(b)) {
                continue;
            }
            capturados++;
            if (b.getTipo() == null || b.getPrecio() == null || b.getCantidad() == null) {
                errores.add("El boleto  necesita nombre, precio y cantidad.");
            } else if (b.getPrecio().signum() < 0 || b.getCantidad() < 0) {
                errores.add("El precio y la cantidad del boleto no pueden ser negativos.");
            } else {
                totalLugares += b.getCantidad();
            }
        }
        if (recinto != null) {
            Integer capacidad = recinto.getCroquis().getCapacidad();
            if (capacidad != null && totalLugares > capacidad) {
                errores.add("Su superó la capacidad máxima del recinto.");
            }
        }
        if (publicar) {
            if (borrador.getCategoria() == null) {
                errores.add("Selecciona una categoría para publicar el evento.");
            }
            if (capturados == 0) {
                errores.add("Agrega al menos un tipo de boleto para publicar el evento.");
            }
        }
        if (borrador.getEventoId() != null) {
            eventoRepository.findById(borrador.getEventoId()).ifPresent(evento -> errores.addAll(erroresDeEdicion(evento, borrador)));
        }
        return errores;
    }
    //Por fín después de mucho tiempo ahora si se pueden crear eventos
    @Transactional
    public Evento crearEvento(EventoBorradorDTO borrador, Organizador organizador, EstadoEvento estado, MultipartFile imagen) {
        List<String> errores = validarBorrador(borrador, estado == EstadoEvento.PUBLICADO);
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join(" ", errores));
        }
        Evento evento = new Evento();
        evento.setNombre(borrador.getNombre());
        evento.setDescripcion(borrador.getDescripcion());
        evento.setCategoria(borrador.getCategoria());
        evento.setFecha(LocalDateTime.of(borrador.getFecha(), borrador.getHora()));
        String urlImagen = borrador.getUrlImagen();
        if (imagen != null && !imagen.isEmpty()) {
            String nueva = guardarImagenCloudinary(imagen);
            if (nueva != null) {
                urlImagen = nueva;
            }
        }
        evento.setUrlImagen(urlImagen);
        evento.setEstado(estado);
        evento.setOrganizador(organizador);
        evento.setRecinto(recintoRepository.findById(borrador.getRecintoId()).orElseThrow());
        evento.setArtistas(new ArrayList<>(artistaRepository.findAllById(borrador.getArtistaIds())));
        for (BoletoBorradorDTO b : borrador.getBoletos()) {
            if (!tieneDatos(b)) {
                continue;
            }
            Boleto boleto = new Boleto();
            boleto.setTipo(b.getTipo());
            boleto.setPrecio(b.getPrecio());
            boleto.setCantidadTotal(b.getCantidad());
            boleto.setCantidadDisponibles(b.getCantidad());
            boleto.setDescripcion(b.getDescripcion());
            evento.agregarBoleto(boleto);
        }
        return eventoRepository.save(evento);
    }

    @Transactional(readOnly = true)
    public Optional<Evento> detalleOrganizador(Long eventoId, Long organizadorId) {
        return eventoRepository.findById(eventoId)
                .filter(e -> e.getOrganizador().getId().equals(organizadorId));
    }
    public boolean puedeEditar(Evento evento) {
        return evento.getEstado() != EstadoEvento.CANCELADO && evento.getEstado() != EstadoEvento.FINALIZADO;
    }
    @Transactional(readOnly = true)
    public Optional<EventoBorradorDTO> borradorParaEditar(Long eventoId, Long organizadorId) {
        return detalleOrganizador(eventoId, organizadorId).filter(this::puedeEditar).map(EventoBorradorDTO::desde);
    }
    @Transactional
    public Evento actualizarEvento(EventoBorradorDTO borrador, Organizador organizador, EstadoEvento nuevoEstado,  MultipartFile imagen) {

        Evento evento = detalleOrganizador(borrador.getEventoId(), organizador.getId()).filter(this::puedeEditar).orElseThrow(() -> new IllegalArgumentException("El evento ya no existe o no se puede editar."));
        EstadoEvento estadoFinal = nuevoEstado != null ? nuevoEstado : evento.getEstado();
        List<String> errores = validarBorrador(borrador, estadoFinal != EstadoEvento.BORRADOR);
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join(" ", errores));
        }
        Recinto recintoNuevo = recintoRepository.findById(borrador.getRecintoId()).orElseThrow();
        LocalDateTime fechaNueva = LocalDateTime.of(borrador.getFecha(), borrador.getHora());

        registrarCambios(evento, organizador, fechaNueva, recintoNuevo, borrador.getDescripcion());

        evento.setNombre(borrador.getNombre());
        evento.setDescripcion(borrador.getDescripcion());
        evento.setCategoria(borrador.getCategoria());
        evento.setFecha(fechaNueva);
        String urlImagen = borrador.getUrlImagen();
        if (imagen != null && !imagen.isEmpty()) {
            String nueva = guardarImagenCloudinary(imagen);
            if (nueva != null) {
                urlImagen = nueva;
            }
        }
        evento.setUrlImagen(urlImagen);
        evento.setRecinto(recintoNuevo);
        evento.setEstado(estadoFinal);
        evento.getArtistas().clear();
        evento.getArtistas().addAll(artistaRepository.findAllById(borrador.getArtistaIds()));
        sincronizarBoletos(evento, borrador.getBoletos());

        return eventoRepository.save(evento);
    }

    private void registrarCambios(Evento evento, Organizador organizador, LocalDateTime fechaNueva, Recinto recintoNuevo, String descripcionNueva) {
        if (!evento.getFecha().equals(fechaNueva)) {
            FechaCambioEvento cambio = new FechaCambioEvento();
            cambio.setEvento(evento);
            cambio.setOrganizador(organizador);
            cambio.setValorAnterior(evento.getFecha());
            cambio.setValorNuevo(fechaNueva);
            cambioEventoRepository.save(cambio);
        }
        if (!evento.getRecinto().getId().equals(recintoNuevo.getId())) {
            LugarCambioEvento cambio = new LugarCambioEvento();
            cambio.setEvento(evento);
            cambio.setOrganizador(organizador);
            cambio.setValorAnterior(evento.getRecinto());
            cambio.setValorNuevo(recintoNuevo);
            cambioEventoRepository.save(cambio);
        }
        if (!Objects.equals(evento.getDescripcion(), descripcionNueva)) {
            DescripcionCambioEvento cambio = new DescripcionCambioEvento();
            cambio.setEvento(evento);
            cambio.setOrganizador(organizador);
            cambio.setValorAnterior(evento.getDescripcion());
            cambio.setValorNuevo(descripcionNueva);
            cambioEventoRepository.save(cambio);
        }
    }
    private void sincronizarBoletos(Evento evento, List<BoletoBorradorDTO> filas) {
        Map<Long, Boleto> existentes = evento.getBoletos().stream().collect(Collectors.toMap(Boleto::getId, b -> b));
        Set<Long> conservados = new HashSet<>();
        for (BoletoBorradorDTO fila : filas) {
            if (!tieneDatos(fila)) {
                continue;
            }
            Boleto boleto = fila.getBoletoId() == null ? null : existentes.get(fila.getBoletoId());
            if (boleto == null) {
                boleto = new Boleto();
                boleto.setCantidadDisponibles(fila.getCantidad());
                evento.agregarBoleto(boleto);
            } else {
                conservados.add(boleto.getId());
                boleto.setCantidadDisponibles(Math.max(0, fila.getCantidad() - boleto.getVendidos()));
            }
            boleto.setTipo(fila.getTipo());
            boleto.setPrecio(fila.getPrecio());
            boleto.setCantidadTotal(fila.getCantidad());
            boleto.setDescripcion(fila.getDescripcion());
        }
        evento.getBoletos().removeIf(b -> b.getId() != null && !conservados.contains(b.getId()));
    }

    private List<String> erroresDeEdicion(Evento evento, EventoBorradorDTO borrador) {
        List<String> errores = new ArrayList<>();
        boolean hayTickets = evento.getBoletos().stream().anyMatch(b -> !b.getTickets().isEmpty());
        if (hayTickets && borrador.getRecintoId() != null && !evento.getRecinto().getId().equals(borrador.getRecintoId())) {
            errores.add("No se puede modificar el recinto, el evento ya cuenta con boletos vendidos");
        }
        Map<Long, BoletoBorradorDTO> filas = borrador.getBoletos().stream().filter(f -> f.getBoletoId() != null && tieneDatos(f))
                .collect(Collectors.toMap(BoletoBorradorDTO::getBoletoId, f -> f, (a, b) -> a));
        for (Boleto boleto : evento.getBoletos()) {
            BoletoBorradorDTO fila = filas.get(boleto.getId());
            if (fila == null) {
                if (!boleto.getTickets().isEmpty()) {
                    errores.add("El boleto ya tiene ventas registradas");
                }
            } else if (fila.getCantidad() != null && fila.getCantidad() < boleto.getVendidos()) {
                errores.add("El número de ventas del boleto sobrepasa la capacidad máxima seleccionada");
            }
        }
        return errores;
    }
    private boolean tieneDatos(BoletoBorradorDTO b) {
        return b.getTipo() != null || b.getPrecio() != null || b.getCantidad() != null || b.getDescripcion() != null;
    }

    @Transactional(readOnly = true)
    public List<Ticket> asistentesDelEvento(Evento evento) {
        return evento.getBoletos().stream().flatMap(b -> b.getTickets().stream()).filter(Ticket::esVendido)
                .sorted(Comparator.comparing((Ticket t) -> t.getCompra().getAsistente().getNombre(),
                        String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<Evento> detalleAdmin(Long id) {
        return eventoRepository.findById(id)
                .filter(e -> e.getEstado() != EstadoEvento.BORRADOR);
    }

    @Transactional(readOnly = true)
    public Optional<Long> idOrganizador(Long eventoId) {
        return eventoRepository.findById(eventoId).map(e -> e.getOrganizador().getId());
    }

    /**
     * Cuenta cuántos eventos finalizados tiene el organizador del evento
     * @param evento el evento del que se consulta el organizador
     * @return el total de eventos finalizados del organizador
     */
    @Transactional(readOnly = true)
    public long eventosRealizados(Evento evento) {
        return eventoRepository.countByOrganizador_IdAndEstado(evento.getOrganizador().getId(), EstadoEvento.FINALIZADO);
    }

    @Transactional
    public boolean desactivar(Long id) {
        Optional<Evento> encontrado = eventoRepository.findById(id);
        if (encontrado.isEmpty() || !puedeDesactivar(encontrado.get())) {
            return false;
        }
        Evento evento = encontrado.get();
        evento.setEstado(EstadoEvento.CANCELADO);
        eventoRepository.save(evento);
        return true;
    }

    @Transactional
    public boolean reactivar(Long id) {
        Optional<Evento> encontrado = eventoRepository.findById(id);
        if (encontrado.isEmpty() || !puedeReactivar(encontrado.get())) {
            return false;
        }
        Evento evento = encontrado.get();
        evento.setEstado(EstadoEvento.PUBLICADO);
        eventoRepository.save(evento);
        return true;
    }

    public boolean puedeDesactivar(Evento evento) {
        return evento.getEstado() == EstadoEvento.PUBLICADO || evento.getEstado() == EstadoEvento.PAUSADO;
    }

    public boolean puedeReactivar(Evento evento) {
        return evento.getEstado() == EstadoEvento.CANCELADO && evento.getFecha().isAfter(LocalDateTime.now());
    }

    private boolean coincideTexto(Evento evento, String texto) {
        if (texto.isEmpty()) {
            return true;
        }
        return contiene(evento.getNombre(), texto)
                || contiene(evento.getRecinto().getNombre(), texto)
                || contiene(evento.getOrganizador().getNombre(), texto);
    }

    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase(Locale.ROOT).contains(texto);
    }

    private boolean coincideFecha(LocalDateTime fecha, String filtro, LocalDateTime ahora) {
        if (filtro == null || filtro.isBlank()) {
            return true;
        }
        return switch (filtro) {
            case "proximos" -> !fecha.isBefore(ahora);
            case "semana" -> !fecha.isBefore(ahora) && !fecha.isAfter(ahora.plusDays(7));
            case "mes" -> !fecha.isBefore(ahora) && !fecha.isAfter(ahora.plusDays(30));
            case "pasados" -> fecha.isBefore(ahora);
            default -> true;
        };
    }

    private boolean coincidePrecio(BigDecimal desde, String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return true;
        }
        if (desde == null) {
            return false;
        }
        return switch (filtro) {
            case "bajo" -> desde.compareTo(LIMITE_BAJO) <= 0;
            case "medio" -> desde.compareTo(LIMITE_BAJO) > 0 && desde.compareTo(LIMITE_MEDIO) <= 0;
            case "alto" -> desde.compareTo(LIMITE_MEDIO) > 0;
            default -> true;
        };
    }

    private String guardarImagenCloudinary(MultipartFile imagen) {
        try {
            Map uploadResult = cloudinary.uploader().upload(imagen.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            return null;
        }
    }

    public void guardarImagenBorrador(EventoBorradorDTO borrador, MultipartFile imagen) {
        if (imagen != null && !imagen.isEmpty()) {
            String nueva = guardarImagenCloudinary(imagen);
            if (nueva != null) {
                borrador.setUrlImagen(nueva);
            }
        }
    }
}
