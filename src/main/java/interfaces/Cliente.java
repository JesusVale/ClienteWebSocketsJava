/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import entidades.Contacto;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jegav
 */
public interface Cliente {
    public void nuevoCliente(List<Map<String, String>> clientes);
    public void nuevoMensaje(String mensaje);
    public void nuevoContacto(Contacto contacto);
}
