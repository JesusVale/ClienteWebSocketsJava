/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entidades.Contacto;
import entidades.Peticion;
import interfaces.Cliente;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import javax.websocket.Session;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;

/**
 *
 * @author jegav
 */
@ClientEndpoint
public class ClientWS {
    
    private Session session;
    static List<Cliente> clientes = new ArrayList();
    
    public void suscribe(Cliente cliente){
        clientes.add(cliente);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Cliente conectado");
    }
    
    @OnMessage
    public void onMessage(String message) {
        System.out.println(message);
        Gson gson = new Gson();
        Map<String, Object> peticion = gson.fromJson(message, new TypeToken<Map<String, Object>>(){}.getType());
        System.out.println(peticion.get("tipo"));
        switch((String) peticion.get("tipo")){
            case "listaUsuarios":
                for(Cliente cl: clientes){
                    List<Map<String, String>> clientes = (List<Map<String, String>>) peticion.get("datos");
                    cl.nuevoCliente(clientes);
                }
                break;
            case "mensaje":
                for(Cliente cl: clientes){
                    cl.nuevoMensaje((String) peticion.get("datos"));
                }
                break;
            case "contacto":
                for(Cliente cl: clientes){
                    Map<String, Object> contactoMap = (Map<String, Object>) peticion.get("datos");
                    Double edad = (Double) contactoMap.get("edad");
                    Contacto contacto = new Contacto((String) contactoMap.get("nombre"),
                                                    (String) contactoMap.get("email"),
                                                    edad.intValue());
                    cl.nuevoContacto(contacto);
                }
                break;
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error: " + throwable.getMessage());
    }
    
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket closed: " + closeReason);
        this.session = null;
    }
    
    public void connectToServer(String uri) throws URISyntaxException, DeploymentException, IOException{
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, new URI(uri));
    }
    
    public void sendMessage(Peticion peticion){
        Gson gson = new Gson();
        String peticionStr = gson.toJson(peticion);
        this.session.getAsyncRemote().sendText(peticionStr);
    }
    
    public void sendContacto(Contacto contacto, String alcance){
        Gson gson = new Gson();
        String contactoJSON = gson.toJson(contacto);
        String peticionStr = gson.toJson(new Peticion(alcance, "contacto", contactoJSON));
        this.session.getAsyncRemote().sendText(peticionStr);
    }
    
}
