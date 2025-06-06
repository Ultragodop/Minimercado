package com.project.minimercado.controllers.chat;

import com.project.minimercado.dto.chat.CrearSalaRequest;
import com.project.minimercado.model.bussines.Usuario;
import com.project.minimercado.model.chat.SalaChat;
import com.project.minimercado.services.chat.SalaChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;

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
        // Obtenemos todas las salas (entidades) y devolvemos solo sus nombres
        List<SalaChat> salas = salaChatService.obtenerTodasLasSalas();
        List<String> nombres = salas.stream()
                .map(SalaChat::getNombre)
                .collect(Collectors.toList());
        return ResponseEntity.ok(nombres);
    }
    @GetMapping("/userid")
    public ResponseEntity<Long> obteneridporusuario(@RequestParam String nombre) {
        Long v = salaChatService.obteneridporusuario(nombre);
        return ResponseEntity.ok(v);
    }

}

