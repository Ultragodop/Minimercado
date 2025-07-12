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
        salaChatRepository.save(nuevaSala); // genera ID

        List<SalaUsuario> usuariosAutorizados = new ArrayList<>();
        usuariosAutorizados.add(new SalaUsuario(nuevaSala, creador)); // Agrega el creador como usuario autorizado
        for (Long idUsuario : request.getUsuariosAutorizadosIds()) {
            Usuario usuario = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            SalaUsuario su = new SalaUsuario();

            SalaUsuarioId id = new SalaUsuarioId(nuevaSala.getId(), usuario.getId());
            su.setId(id);

            su.setSala(nuevaSala);
            su.setUsuario(usuario);

            usuariosAutorizados.add(su);
        }

        salaUsuarioRepository.saveAll(usuariosAutorizados);

        return nuevaSala;
    }


    public List<SalaChat> ObtenerTodasPorPermiso(Long usuarioId) {
        List<SalaChat> salaChats= salaChatRepository.findAll();
        List<SalaUsuario> salaUsuarios= salaUsuarioRepository.findAll();
        List<SalaChat> salaswhereuserhaspermission= new ArrayList<>();
        for (SalaChat salaChat : salaChats) {
            for (SalaUsuario salaUsuario : salaUsuarios) {
                if (salaUsuario.getSala().getId().equals(salaChat.getId()) &&
                        salaUsuario.getUsuario().getId().equals(usuarioId)) {

                    salaswhereuserhaspermission.add(salaChat);
                    System.out.println(salaUsuario.getSala().getNombre() + " tiene permiso para el usuario: " + salaUsuario.getUsuario().getNombre());
                }
            }
        }
        if (salaswhereuserhaspermission.isEmpty()) {
            System.out.println("No tienes permisos para ninguna sala");
        } else {
            System.out.println("Tienes permisos para las siguientes salas:");
            for (SalaChat sala : salaswhereuserhaspermission) {
                System.out.println(sala.getNombre());
            }
        }
        return salaswhereuserhaspermission;

    }
    public boolean PermitirConexionPorSala(Long usuarioId, String salaNombre) {
        List<SalaChat> salaChats = salaChatRepository.findAll();
        List<SalaUsuario> salaUsuarios = salaUsuarioRepository.findAll();
        for (SalaChat salaChat : salaChats) {
            if (salaChat.getNombre().equals(salaNombre)) {
                for (SalaUsuario salaUsuario : salaUsuarios) {
                    if (salaUsuario.getSala().getId().equals(salaChat.getId()) &&
                            salaUsuario.getUsuario().getId().equals(usuarioId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



}
