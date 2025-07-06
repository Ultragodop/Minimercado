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

    @GetMapping("/todas")
    public ResponseEntity<List<String>> listarSalas() {
        List<SalaChat> salas = salaChatService.obtenerTodasLasSalas();
        List<String> nombres = salas.stream()
                .map(SalaChat::getNombre)
                .collect(Collectors.toList());
        return ResponseEntity.ok(nombres);
    }

    @GetMapping("/userid")
    public ResponseEntity<Long> obteneridporusuario(@RequestParam String nombre) {
        Long v = salaChatService.obteneridporusuario(nombre);
        if (v == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(v);
    }

}
