package dominioPlataformaEventos.entidades;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import dominioPlataformaEventos.enums.EstadoEvento;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author julian izaguirre, Erick Armenta, Willian Tolano
 */
public class Evento {
     private Long id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fecha;
    private Direccion direccion;
    private String categoria;
    private String urlImagen;
    private Integer capacidad;
    private EstadoEvento estado;
    private List<Boleto> boletos = new ArrayList<>();
    private List<CambioEvento> historialCambios = new ArrayList<>();

    public Evento(String nombre, String descripcion, LocalDateTime fecha, Direccion direccion,
                  String categoria, String urlImagen, Integer capacidad) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.direccion = direccion;
        this.categoria = categoria;
        this.urlImagen = urlImagen;
        this.capacidad = capacidad;
        this.estado = EstadoEvento.BORRADOR;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public EstadoEvento getEstado() {
        return estado;
    }

    public void setEstado(EstadoEvento estado) {
        this.estado = estado;
    }

    public List<Boleto> getBoletos() {
        return boletos;
    }

    public void agregarBoleto(Boleto boleto) {
        boletos.add(boleto);
        boleto.setEvento(this);
    }

    public List<CambioEvento> getHistorialCambios() {
        return historialCambios;
    }

    public void agregarCambio(CambioEvento cambio) {
        historialCambios.add(cambio);
        cambio.setEvento(this);
    }
}
