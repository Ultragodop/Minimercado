package com.project.minimercado.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercado.dto.chat.ChatMessageDTO;

import com.project.minimercado.model.chat.ChatMessage;
import com.project.minimercado.model.chat.SalaChat;
import com.project.minimercado.repository.bussines.UsuarioRepository;
import com.project.minimercado.repository.chat.ChatMessageRepository;
import com.project.minimercado.repository.chat.SalaChatRepository;
import com.project.minimercado.services.auth.JWT.JWTService;
import com.project.minimercado.services.chat.EncryptionUtils;
import com.project.minimercado.services.chat.SalaChatService;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private ChatMessageRepository chatRepository;
    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);
    private final SalaChatRepository salaChatRepo;

    private final UsuarioRepository usuarioRepo;
    private final ChatMessageRepository chatMessageRepository;
    private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private final Map<String, Set<WebSocketSession>> salasSessions = new ConcurrentHashMap<>();
   private final Map<String, String> sessionIdToSala = new ConcurrentHashMap<>();
    private final Map<String , String > salatousuario = new ConcurrentHashMap<>();
    private final Map<String, String> usuarioToken= new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String , String > emisorReceptor= new ConcurrentHashMap<>();
    private SalaChatService salaChatService;
    private final JWTService jwtService;

    //Una sesion tiene varios atributos, entre ellos el username, url, etc.
    //Por lo que se puede acceder a los atributos de la sesión WebSocket
    //Tambien tiene headers, que son los headers de la solicitud HTTP inicial que despues se convierte en WebSocket: http:// -> ws://
    //Se pueden obtener los headers de la sesión WebSocket, por ejemplo, para obtener el token JWT o cualquier otro dato que se haya enviado en la solicitud inicial
    //Para que el usuario no vea el token JWT en la URL, se puede enviar como un header personalizado en la solicitud HTTP inicia:D


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");
        session.setTextMessageSizeLimit(2048 * 1024); // Establecer límite de tamaño del mensaje a 2 MB
        session.setBinaryMessageSizeLimit(2048 * 1024); // Establecer límite de tamaño del mensaje binario a 2 MB
        logger.info("Headers de la sesion, {}", session.getHandshakeHeaders());
        if (username == null) {
            logger.warn("No se encontró el username en la sesión WebSocket");
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("No autorizado: usuario no encontrado"));
            return;
        }



        String path = Objects.requireNonNull(session.getUri()).getPath();
        if (!path.startsWith("/chat/")) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Ruta no válida"));
            return;
        }

        String salaNombre = path.substring(path.lastIndexOf("/") + 1);
        if (salaNombre.isEmpty()) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Nombre de sala no proporcionado"));
            return;
        }
        logger.info("Usuario {} conectado a la sala {}", username, salaNombre);
        Optional<SalaChat> salaOpt = salaChatRepo.findByNombre(salaNombre);
        if (salaOpt.isEmpty()) {
            logger.warn("Sala {} no encontrada", salaNombre);
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Sala no encontrada"));
            return;
        }

        long id= usuarioRepo.getIdUsuario(username);
        boolean permitir= salaChatService.PermitirConexionPorSala(id , salaNombre);
        if(!permitir) {
            logger.warn("Usuario {} no tiene permiso para unirse a la sala {}", username, salaNombre);
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("No autorizado: usuario no tiene permiso para unirse a la sala"));
            return;
        }
        logger.info("Usuario {} tiene permiso para unirse a la sala {}", username, salaNombre);

        salasSessions.computeIfAbsent(salaNombre, k -> ConcurrentHashMap.newKeySet()).add(session);

        salatousuario.put(salaNombre, username);
        sessionIdToSala.put(session.getId(), salaNombre);
        String receptor=salaChatService.encontrarusuarioreceptor(salatousuario.get(sessionIdToSala.get(session.getId())), sessionIdToSala.get(session.getId()));
        emisorReceptor.put(username, receptor);
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
        Boolean verificado = verificarMensajeConToken(session);
        String receptor= emisorReceptor.get(salatousuario.get(sessionIdToSala.get(session.getId())));
        logger.info("El usuario emisor tiene un receptor? {}", receptor != null);
        logger.info("El receptor del emisor {} es {}", salatousuario.get(sessionIdToSala.get(session.getId())), receptor);

    // demasiados logs fucking inutiles, pero bueno, asi se aprende
        if(verificado && receptor!=null) {
            String salaDelEmisor = sessionIdToSala.get(session.getId());
            if (salaDelEmisor == null) {
                logger.warn("Sesión {} no está registrada en ninguna sala", session.getId());
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Sesión no registrada en ninguna sala"));
                return;
            }

            ChatMessageDTO chatMessageDTO = mapper.readValue(message.getPayload(), ChatMessageDTO.class);
            logger.info("Mensaje recibido: {}", chatMessageDTO.getMensaje());


            logger.info("Cantidad de bytes del mensaje: {} Bytes", message.getPayloadLength());
            String jsonMessage = mapper.writeValueAsString(chatMessageDTO);
            Set<WebSocketSession> sesiones = salasSessions.getOrDefault(salaDelEmisor, Collections.emptySet());
            //definicion de las sesiones de sala a las que se enviara el mensaje


            //FUking logger de re mierda, no deja imprimir el set de usuarios conectados
            //Set<String> usuariosConectados = salasSessions.get(salaDelEmisor)
              //      .stream()
                //    .map(s -> s.getAttributes().get("username").toString())
                  //  .collect(Collectors.toSet());
            //logger.info("Usuarios conectados en la sala {}: {}", salaDelEmisor, usuariosConectados);

            //Iterator una interfaz de java que permite recorrer una colección de objetos
            //Collection es una interfaz que representa una colección de objetos
            //Set es una colección que no permite elementos duplicados :D
            Iterator<WebSocketSession> it = sesiones.iterator();
            while (it.hasNext()) {
                WebSocketSession s = it.next();

                if (!s.isOpen()) {
                    it.remove();
                    continue;
                }
                if (!s.getId().equals(session.getId())) {
                    String nombre= (String) s.getAttributes().get("username");
                    logger.info("Enviando mensaje a la sesión {} del usuario {}", s.getId(), nombre);
                    s.sendMessage(new TextMessage(jsonMessage));
                }
            }


            long start = System.currentTimeMillis();
            guardarMensaje(salaDelEmisor, chatMessageDTO.getUsuario(), chatMessageDTO.getMensaje(), timestamp);
            long end = System.currentTimeMillis();
            logger.info("Mensaje guardado en la base de datos en {} ms", (end - start));
        }
        else {
            logger.warn("Token no válido o no proporcionado en la sesión {}", session.getId());
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Token no válido o no proporcionado"));
        }
    }



    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        logger.warn("Conexión WebSocket cerrada: {}, estado: {}", session.getId(), status);

        String path = Objects.requireNonNull(session.getUri()).getPath();
        String sala = path.substring(path.lastIndexOf("/") + 1);


        Set<WebSocketSession> sesiones = salasSessions.get(sala);
        if (sesiones != null) {

            sesiones.remove(session);

            if (sesiones.isEmpty()) {
                salasSessions.remove(sala);
                logger.info("Sala '{}' vacía, eliminada del registro.", sala);
            }
        }
    }
    public Boolean verificarMensajeConToken(WebSocketSession session) {

        logger.info("Sesion con token header: {}" ,session.getHandshakeHeaders().get("Authorization"));
        if (session.getHandshakeHeaders().get("Authorization") == null) {
            logger.warn("No se encontró el header Authorization en la sesión WebSocket");

            return false;
        }
        String tokenHeader = Objects.requireNonNull(session.getHandshakeHeaders().get("Authorization")).getFirst();
        if (!tokenHeader.startsWith("Bearer ")) {
            logger.warn("Token no proporcionado en el header Authorization");
            return false;
        }
        String token = tokenHeader.substring("Bearer ".length());
        logger.info("Token obtenido en la solicitud de mensaje: {}", token);

        //Como estaba antes
        /**URI uri = session.getUri();
        logger.info("URI de la sesión WebSocket: {}", uri);
        assert uri != null;
        String query = uri.getQuery();
        logger.info("JWT query: " + query);
        String token = null;

        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    token = param.substring("token=".length());
                    logger.info("Token obtenido en la solicitud de mensaje: {}", token);
                    break;
                }
            }
        }*/
        if (token.isEmpty()) {
            logger.warn("Token no proporcionado en el mensaje WebSocket");
            return false;
        }
        if (!jwtService.isValidTokenFormat(token) || !jwtService.isTokenStored(token)) {
            logger.warn("Token no valido en la solicitud de mensaje");
            return false;
        }
        logger.info("Token válido en la solicitud de mensaje: {}", token);
        return true;
    }
    public void guardarMensaje(String salaDelEmisor, String usuario, String mensaje, Timestamp timestamp) {


        try {
            SalaChat salaChat = salaChatRepo.findByNombre(salaDelEmisor)
                    .orElseThrow(() -> new IllegalArgumentException("Sala no encontrada: " + salaDelEmisor));


            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSala(salaChat);
            chatMessage.setUsuario(usuarioRepo.findByNombreas(usuario)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuario)));

            chatMessage.setTimestamp(timestamp.toLocalDateTime());
            chatRepository.save(chatMessage);
        }catch (Exception e)
        {
            logger.error("Error al guardar el mensaje: {}", e.getMessage());
            e.printStackTrace();
        }

    }

}

