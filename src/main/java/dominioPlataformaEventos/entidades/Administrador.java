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
public class Administrador extends Usuario {
    
    public Administrador(String nombre, String correo, String hashContrasenia, Boolean activo) {
        super(nombre, correo, hashContrasenia, TipoUsuario.ORGANIZADOR, activo);
    }
    
}
