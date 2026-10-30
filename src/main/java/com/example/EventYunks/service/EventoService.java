package com.example.EventYunks.service;

import com.example.EventYunks.dto.EventoDetalle;
import com.example.EventYunks.dto.EventoTarjeta;
import com.example.EventYunks.model.Artista;
import com.example.EventYunks.model.Boleto;
import com.example.EventYunks.model.Direccion;
import com.example.EventYunks.model.Evento;
import com.example.EventYunks.model.Organizador;
import com.example.EventYunks.model.Recinto;
import com.example.EventYunks.model.enums.EstadoEvento;
import com.example.EventYunks.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventoService {

    private static final Locale MEXICO = Locale.forLanguageTag("es-MX");
    private static final DateTimeFormatter FECHA_CORTA = DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm 'h'", MEXICO);
    private static final DateTimeFormatter FECHA_LARGA = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy · HH:mm 'h'", MEXICO);
    private static final BigDecimal LIMITE_BAJO = new BigDecimal("500");
    private static final BigDecimal LIMITE_MEDIO = new BigDecimal("1000");

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Transactional(readOnly = true)
    public List<EventoTarjeta> catalogoAdmin(String buscar, String categoria, String fecha, String precio) {
        LocalDateTime ahora = LocalDateTime.now();
        String texto = buscar == null ? "" : buscar.trim().toLowerCase(Locale.ROOT);

        List<Evento> filtrados = eventoRepository.findByEstadoNot(EstadoEvento.BORRADOR).stream()
                .filter(e -> coincideTexto(e, texto))
                .filter(e -> categoria == null || categoria.isBlank() || categoria.equalsIgnoreCase(e.getCategoria()))
                .filter(e -> coincideFecha(e.getFecha(), fecha, ahora))
                .filter(e -> coincidePrecio(precioDesde(e), precio))
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

        return ordenados.stream().map(this::aTarjeta).toList();
    }

    @Transactional(readOnly = true)
    public List<String> categorias() {
        return eventoRepository.categoriasDeEventosExcepto(EstadoEvento.BORRADOR);
    }

    @Transactional(readOnly = true)
    public Optional<EventoDetalle> detalleAdmin(Long id) {
        return eventoRepository.findById(id)
                .filter(e -> e.getEstado() != EstadoEvento.BORRADOR)
                .map(this::aDetalle);
    }

    @Transactional(readOnly = true)
    public Optional<Long> idOrganizador(Long eventoId) {
        return eventoRepository.findById(eventoId).map(e -> e.getOrganizador().getId());
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

    private boolean puedeDesactivar(Evento evento) {
        return evento.getEstado() == EstadoEvento.PUBLICADO || evento.getEstado() == EstadoEvento.PAUSADO;
    }

    private boolean puedeReactivar(Evento evento) {
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

    private BigDecimal precioDesde(Evento evento) {
        return evento.getBoletos().stream()
                .map(Boleto::getPrecio)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private EventoTarjeta aTarjeta(Evento evento) {
        BigDecimal desde = precioDesde(evento);
        return EventoTarjeta.builder()
                .id(evento.getId())
                .nombre(evento.getNombre())
                .imagen(limpiar(evento.getUrlImagen()))
                .etiqueta(etiqueta(evento.getEstado()))
                .claseEtiqueta(claseEtiqueta(evento.getEstado()))
                .cancelado(evento.getEstado() == EstadoEvento.CANCELADO)
                .fecha(evento.getFecha().format(FECHA_CORTA))
                .recinto(evento.getRecinto().getNombre())
                .organizador(evento.getOrganizador().getNombre())
                .precio(textoPrecio(evento.getEstado(), desde))
                .build();
    }

    private EventoDetalle aDetalle(Evento evento) {
        Organizador organizador = evento.getOrganizador();
        Recinto recinto = evento.getRecinto();

        List<EventoDetalle.ArtistaLinea> artistas = evento.getArtistas().stream()
                .map(a -> new EventoDetalle.ArtistaLinea(
                        a.getNombre(),
                        textoOpcional(a.getGeneroMusical(), "Sin género"),
                        limpiar(a.getUrlImagen()),
                        iniciales(a)))
                .toList();

        List<EventoDetalle.BoletoLinea> boletos = evento.getBoletos().stream()
                .map(b -> new EventoDetalle.BoletoLinea(
                        b.getTipo(),
                        textoOpcional(b.getDescripcion(), "Sin descripción"),
                        dinero(b.getPrecio()),
                        disponibilidad(b)))
                .toList();

        return EventoDetalle.builder()
                .id(evento.getId())
                .nombre(evento.getNombre())
                .categoria(limpiar(evento.getCategoria()))
                .descripcion(textoOpcional(evento.getDescripcion(), "Este evento no tiene descripción."))
                .imagen(limpiar(evento.getUrlImagen()))
                .etiqueta(etiqueta(evento.getEstado()))
                .claseEtiqueta(claseEtiqueta(evento.getEstado()))
                .cancelado(evento.getEstado() == EstadoEvento.CANCELADO)
                .fechaLarga(evento.getFecha().format(FECHA_LARGA))
                .recinto(recinto.getNombre())
                .direccion(direccion(recinto.getDireccion()))
                .organizadorNombre(organizador.getNombre())
                .organizadorCorreo(organizador.getCorreo())
                .organizadorActivo(Boolean.TRUE.equals(organizador.getActivo()))
                .eventosRealizados(eventoRepository.countByOrganizador_IdAndEstado(organizador.getId(), EstadoEvento.FINALIZADO))
                .artistas(artistas)
                .boletos(boletos)
                .puedeDesactivar(puedeDesactivar(evento))
                .puedeReactivar(puedeReactivar(evento))
                .build();
    }

    private String etiqueta(EstadoEvento estado) {
        return switch (estado) {
            case BORRADOR -> "Borrador";
            case PUBLICADO -> "En Venta";
            case PAUSADO -> "Pausado";
            case CANCELADO -> "Cancelado";
            case FINALIZADO -> "Finalizado";
        };
    }

    private String claseEtiqueta(EstadoEvento estado) {
        return switch (estado) {
            case CANCELADO -> "cancelado";
            case FINALIZADO -> "finalizado";
            case PAUSADO -> "espera";
            default -> "";
        };
    }

    private String textoPrecio(EstadoEvento estado, BigDecimal desde) {
        return switch (estado) {
            case PUBLICADO -> desde == null ? "Sin boletos registrados" : "Desde " + dinero(desde);
            case PAUSADO -> "Venta pausada";
            case CANCELADO -> "Venta suspendida";
            case FINALIZADO -> "Ya ocurrió · sin venta de boletos";
            case BORRADOR -> "Borrador";
        };
    }

    private String dinero(BigDecimal valor) {
        if (valor == null) {
            return "$0";
        }
        BigDecimal sinCeros = valor.stripTrailingZeros();
        if (sinCeros.scale() <= 0) {
            return "$" + String.format(Locale.US, "%,d", sinCeros.toBigInteger());
        }
        return "$" + String.format(Locale.US, "%,.2f", valor);
    }

    private String disponibilidad(Boleto boleto) {
        if (boleto.getCantidadDisponibles() == null || boleto.getCantidadTotal() == null) {
            return null;
        }
        return boleto.getCantidadDisponibles() + " de " + boleto.getCantidadTotal() + " disponibles";
    }

    private String direccion(Direccion direccion) {
        if (direccion == null) {
            return null;
        }
        String calle = limpiar(direccion.getCalle());
        String numero = limpiar(direccion.getNumero());
        String callenumero = calle == null ? null : (numero == null ? calle : calle + " " + numero);
        List<String> partes = new ArrayList<>();
        for (String parte : new String[]{callenumero, limpiar(direccion.getColonia()), limpiar(direccion.getCiudad())}) {
            if (parte != null) {
                partes.add(parte);
            }
        }
        return partes.isEmpty() ? null : String.join(", ", partes);
    }

    private String iniciales(Artista artista) {
        String nombre = artista.getNombre() == null ? "" : artista.getNombre().trim();
        if (nombre.isEmpty()) {
            return "?";
        }
        String[] palabras = nombre.split("\\s+");
        String resultado = palabras[0].substring(0, 1);
        if (palabras.length > 1) {
            resultado += palabras[1].substring(0, 1);
        }
        return resultado.toUpperCase(MEXICO);
    }

    private String textoOpcional(String valor, String porDefecto) {
        String limpio = limpiar(valor);
        return limpio == null ? porDefecto : limpio;
    }

    private String limpiar(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}
