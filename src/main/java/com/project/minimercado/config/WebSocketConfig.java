package com.project.minimercado.config;

import com.project.minimercado.exception.ChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Ahora maneja rutas como "/chat/general", "/chat/programadores", etc.
        registry.addHandler(new ChatWebSocketHandler(), "/chat/**")
                .setAllowedOrigins("*");
    }
}


/*
🎯 Objetivo:
Permitir que:

Se puedan crear nuevos chats desde el frontend.

Solo ciertos usuarios autorizados puedan ver y acceder a esos chats.

🧠 Conceptos clave a manejar:
Sala de chat (ChatRoom):

Es un canal o instancia de comunicación.

Tiene un id, nombre, y una lista de usuarios autorizados.

Usuario autenticado:

Cada acción se hace como un usuario identificado (por nombre, ID, o token).

Permisos por sala:

El backend debe validar si un usuario tiene permiso para acceder a una sala.

🧱 Estructura general
1. Modelo de ChatRoom (sala)
Cada sala debería tener:

ID único (ej. "sala123")

Nombre visible (ej. "Chat de desarrollo")

Lista de usuarios permitidos (puede ser por ID, username o roles)

2. Creación de salas
El frontend permite crear una nueva sala.

El usuario que la crea define:

El nombre

Qué usuarios están invitados (por selección)

Esta información se envía al backend.

3. Validación de acceso al entrar en una sala
Cuando un usuario quiere abrir un chat:

El frontend solicita unirse a una sala por su ID.

El backend:

Verifica que ese usuario esté autorizado.

Si lo está, le permite conectarse al WebSocket o recibir mensajes.

4. Front-end dinámico
Mostrar la lista de salas disponibles para ese usuario (el backend solo devuelve las que le corresponden).

Al hacer clic en una sala, se conecta a una URL tipo:
ws://tu-server/chat/sala123
(cada sala tiene su propio canal o subcanal).

5. Seguridad
Si alguien intenta conectarse manualmente a una sala que no le corresponde, el backend debe:

Detectarlo (verificando su identidad).

Rechazar la conexión o cerrar el canal.

Idealmente usás autenticación por token o sesión activa.

🔁 Flujo resumido
Usuario A crea sala "DevTeam", y agrega a Usuario B y C.

Backend guarda esa sala con la lista de usuarios.

Usuario B entra al frontend → ve "DevTeam" en su lista.

Usuario B abre el chat → se conecta al WebSocket "ws://.../DevTeam".

Backend valida que B tiene permiso → permite conexión.

Usuario Z (no invitado) intenta entrar → se le niega acceso.




*
 */



