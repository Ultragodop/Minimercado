package com.project.minimercado.services.chat;

import com.project.minimercado.dto.chat.CrearSalaRequest;
import com.project.minimercado.model.bussines.Usuario;
import com.project.minimercado.model.chat.SalaChat;
import com.project.minimercado.model.chat.SalaUsuario;
import com.project.minimercado.model.chat.SalaUsuarioId;
import com.project.minimercado.repository.bussines.UsuarioRepository;
import com.project.minimercado.repository.chat.SalaChatRepository;
import com.project.minimercado.repository.chat.salaUsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SalaChatService {
    int maxusuariosporsala=2; // Chat de 2 usuarios
    int contador=0;
    private final salaUsuarioRepository salaUsuarioRepository;
    private final UsuarioRepository usuarioRepository;


    private final SalaChatRepository salaChatRepository;

    public SalaChatService(UsuarioRepository usuarioRepository, salaUsuarioRepository salausuariorepositorys, SalaChatRepository salaChatRepository) {
        this.usuarioRepository = usuarioRepository;
        this.salaChatRepository = salaChatRepository;
        this.salaUsuarioRepository = salausuariorepositorys;
    }

    public SalaChat crearSala(CrearSalaRequest request) {
        SalaChat nuevaSala = new SalaChat();
        nuevaSala.setNombre(request.getNombre());

        Usuario creador = usuarioRepository.findById(request.getCreadorId())
                .orElseThrow(() -> new RuntimeException("Creador no encontrado"));

        nuevaSala.setCreador(creador);
        salaChatRepository.save(nuevaSala);

        List<SalaUsuario> usuariosAutorizados = new ArrayList<>();
        usuariosAutorizados.add(new SalaUsuario(nuevaSala, creador));

        for (Long idUsuario : request.getUsuariosAutorizadosIds()) {
            contador++;
            if (contador > maxusuariosporsala) {
                throw new RuntimeException("No se pueden agregar más usuarios a la sala. Límite alcanzado.");
            }
            Usuario usuario = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            SalaUsuario su = new SalaUsuario();

            SalaUsuarioId id = new SalaUsuarioId(nuevaSala.getId(), usuario.getId());
            su.setId(id);

            su.setSala(nuevaSala);
            su.setUsuario(usuario);

            usuariosAutorizados.add(su);
        }
        contador=0; //ahi va pq si no, se acumula el contador y no se puede crear una nueva sala (mongolico como no pense en eso)
        salaUsuarioRepository.saveAll(usuariosAutorizados);

        return nuevaSala;
    }


    public List<SalaChat> ObtenerTodasPorPermiso(Long usuarioId) {

        List<SalaUsuario> salaUsuarios = salaUsuarioRepository.findAll();
        List<SalaChat> salaswhereuserhaspermission = new ArrayList<>();
            for (SalaUsuario salaUsuario : salaUsuarios){
                if (salaUsuario.getUsuario().getId().equals(usuarioId)) {
                    salaswhereuserhaspermission.add(salaUsuario.getSala());
                }
        }
        if (salaswhereuserhaspermission.isEmpty()) {
            System.out.println("No tiene fukin permisos para ninguna sala");
        } else {
            System.out.println("Tienes permisos para las siguientes salas:");
            for (SalaChat sala : salaswhereuserhaspermission) {
                System.out.println(sala.getNombre());
            }
        }
        return salaswhereuserhaspermission;

    }

    public boolean PermitirConexionPorSala(Long usuarioId, String salaNombre) {
        List<SalaUsuario> salaUsuarios = salaUsuarioRepository.findAll();
        for (SalaUsuario salaUsuario : salaUsuarios) {
            if(salaUsuario.getUsuario().getId().equals(usuarioId)){
                if (salaUsuario.getSala().getNombre().equals(salaNombre)) {
                    System.out.println("El usuario " + salaUsuario.getUsuario().getNombre() + " tiene permiso para conectarse a la sala " + salaNombre);
                    return true;
                } else {
                    System.out.println("El usuario " + salaUsuario.getUsuario().getNombre() + " no tiene permiso para conectarse a la sala " + salaNombre);
                }
            } //mejoras en la optimizacion de la busqueda, antes 2 busquedas, ahora solo una :D
        }
                return false;
    }
    /**
     * Encuentra el usuario receptor en una sala de chat. toma mira camilo, una sola bosqueda a la base de datos
     * @param emisor El nombre del usuario emisor.
     * @param salaNombre El nombre de la sala de chat.
     * @return El nombre del usuario receptor o null si no se encuentra.
     */
public String encontrarusuarioreceptor(String emisor, String salaNombre){
    List<SalaUsuario> salaUsuarios = salaUsuarioRepository.findAll();
    //primero se busca la relacion donde el emisor es el usuario y la sala es la salaNombre
    //luego se busca el usuario receptor que es el otro usuario de la sala
    //se retorna el nombre del usuario receptor
    // si no se encuentra el usuario receptor || no se encuentra la sala correspondiente a la sala, se retorna null
    // negro de mierda kaka negro de mierda
    for (SalaUsuario salaUsuario : salaUsuarios) {
        if (salaUsuario.getUsuario().getNombre().equals(emisor) && salaUsuario.getSala().getNombre().equals(salaNombre))  {
            for(SalaUsuario salaUsuario1 : salaUsuarios){
                if(salaUsuario1.getSala().getId().equals(salaUsuario.getSala().getId())){
                    System.out.println("El usuario receptor deberia ser: "+salaUsuario1.getUsuario().getNombre());
                    return salaUsuario1.getUsuario().getNombre();
                }
            }
        }
    }
    return null;
}

}
