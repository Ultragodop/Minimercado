package com.project.minimercado.controllers.chat;


/**
 * import com.project.minimercado.model.bussines.Usuario;
 * import com.project.minimercado.model.chat.SalaChat;
 * import com.project.minimercado.model.chat.SalaUsuario;
 * import com.project.minimercado.repository.bussines.UsuarioRepository;
 * import com.project.minimercado.repository.chat.SalaChatRepository;
 * import com.project.minimercado.repository.chat.salaUsuarioRepository;
 * <p>
 * import lombok.RequiredArgsConstructor;
 * import org.springframework.boot.actuate.web.exchanges.HttpExchange;
 * import org.springframework.http.HttpStatus;
 * import org.springframework.http.ResponseEntity;
 * import org.springframework.web.bind.annotation.*;
 * <p>
 * <p>
 * import java.util.List;
 * import java.util.Map;
 * import java.util.Optional;
 * import java.util.stream.Stream;
 *
 * @RestController
 * @RequestMapping("/api/chat")
 * @RequiredArgsConstructor public class ChatRoomController {
 * private final SalaChatRepository salaChatRepo;
 * private final salaUsuarioRepository salaUsuarioRepo;
 * private final UsuarioRepository usuarioRepo;
 * @PostMapping("/private/{otherUserId}") public ResponseEntity<Map<String,String>> getOrCreatePrivateRoom(
 * HttpExchange.Principal principal,
 * @PathVariable Long otherUserId) {
 * <p>
 * // 1) ID del usuario logueado
 * String meUsername = principal.getName();
 * Optional<Usuario> meOpt = Optional.ofNullable(usuarioRepo.findByNombre(meUsername));
 * if (meOpt.isEmpty()) {
 * return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
 * }
 * Long meId = meOpt.get().getId();
 * <p>
 * // 2) Ordenar para el nombre único
 * long low = Math.min(meId, otherUserId);
 * long high = Math.max(meId, otherUserId);
 * String roomName = "user_" + low + "_" + high;
 * <p>
 * // 3) Buscar o crear la sala
 * SalaChat sala = salaChatRepo.findByNombre(roomName)
 * .orElseGet(() -> {
 * SalaChat nueva = new SalaChat();
 * nueva.setNombre(roomName);
 * salaChatRepo.save(nueva);
 * // Asociar ambos usuarios
 * List<SalaUsuario> links = Stream.of(low, high)
 * .map(id -> {
 * Usuario u = usuarioRepo.findById(id)
 * .orElseThrow(); // asumes que existen
 * SalaUsuario su = new SalaUsuario();
 * su.setSala(nueva);
 * su.setUsuario(u);
 * return su;
 * }).toList();
 * salaUsuarioRepo.saveAll(links);
 * return nueva;
 * });
 * <p>
 * // 4) Devolver el nombre de la sala en JSON
 * return ResponseEntity.ok(Map.of("roomName", roomName));
 * }
 * }
 **/