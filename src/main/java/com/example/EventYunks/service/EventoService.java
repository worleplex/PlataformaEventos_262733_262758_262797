package com.example.EventYunks.service;

import com.example.EventYunks.model.Evento;
import com.example.EventYunks.model.enums.EstadoEvento;
import com.example.EventYunks.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventoService {

    private static final BigDecimal LIMITE_BAJO = new BigDecimal("500");
    private static final BigDecimal LIMITE_MEDIO = new BigDecimal("1000");

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
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
}
