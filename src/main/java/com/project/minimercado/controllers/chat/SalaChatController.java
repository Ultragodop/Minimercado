package com.project.minimercado.controllers.chat;

import com.project.minimercado.dto.chat.CrearSalaRequest;
import com.project.minimercado.model.chat.SalaChat;
import com.project.minimercado.services.chat.SalaChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/salas")
public class SalaChatController {


    private final SalaChatService salaChatService;

    public SalaChatController(SalaChatService salaChatService) {
        this.salaChatService = salaChatService;
    }

    @PostMapping("/crear")
    public ResponseEntity<SalaChat> crearSala(@RequestBody CrearSalaRequest request) {
        SalaChat nuevaSala = salaChatService.crearSala(request);
        return ResponseEntity.ok(nuevaSala);
    }

    @GetMapping("/todas-por-permiso/{idUsuario}")
    public ResponseEntity<List<String>> listarSalas(@PathVariable long idUsuario) {
        return ResponseEntity.ok(
                salaChatService.ObtenerTodasPorPermiso(idUsuario).stream()
                        .map(SalaChat::getNombre)
                        .collect(Collectors.toList())
        );
     //ahora si se muestran todas las salas de chat por permiso:D
    }


}
