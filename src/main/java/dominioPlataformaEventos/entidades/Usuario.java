package dominioPlataformaEventos.entidades;

import dominioPlataformaEventos.enums.TipoUsuario;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author julian izaguirre, Erick Armenta, Willian Tolano
 */
public class Usuario {
    private Long id;
    private String nombre;
    private String correo;
    private String hashContrasenia;
    private TipoUsuario rol;
    private Boolean activo;

    public Usuario(String nombre, String correo, String hashContrasenia, TipoUsuario rol, Boolean activo) {
        this.nombre = nombre;
        this.correo = correo;
        this.hashContrasenia = hashContrasenia;
        this.rol = rol;
        this.activo = activo;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getHashContrasenia() {
        return hashContrasenia;
    }

    public void setHashContrasenia(String hashContrasenia) {
        this.hashContrasenia = hashContrasenia;
    }

    public TipoUsuario getRol() {
        return rol;
    }

    public void setRol(TipoUsuario rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
