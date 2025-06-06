package com.project.minimercado.services.chat;

import com.project.minimercado.dto.chat.CrearSalaRequest;
import com.project.minimercado.model.bussines.Usuario;
import com.project.minimercado.model.chat.SalaChat;
import com.project.minimercado.model.chat.SalaUsuario;
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
        for (Long idUsuario : request.getUsuariosAutorizadosIds()) {
            Usuario usuario = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            SalaUsuario su = new SalaUsuario();
            su.setSala(nuevaSala);
            su.setUsuario(usuario);
            usuariosAutorizados.add(su);
        }

        salaUsuarioRepository.saveAll(usuariosAutorizados);

        return nuevaSala;
    }


    public List<SalaChat> obtenerTodasLasSalas() {
        return salaChatRepository.findAll();
    }
    public Long obteneridporusuario(String nombre) {
        return usuarioRepository.getIdUsuario(nombre);

    }

}
