let stompClient = null;

function conectar() {
    const socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Conectado: ' + frame);
        stompClient.subscribe('/topic/mensajes', function (mensaje) {
            mostrarMensaje(mensaje.body);
        });
    });
}

function enviarMensaje() {
    const mensaje = document.getElementById('mensaje').value;
    if (mensaje.trim() !== '') {
        stompClient.send("/app/mensaje", {}, mensaje);
        document.getElementById('mensaje').value = '';
    }
}

function mostrarMensaje(mensaje) {
    const area = document.getElementById('mensajes');
    area.value += mensaje + "\n";
}

document.addEventListener("DOMContentLoaded", function () {
    conectar();
});
