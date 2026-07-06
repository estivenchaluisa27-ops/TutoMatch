(function () {
    'use strict';

    var toggle = document.getElementById('chatToggle');
    var panel = document.getElementById('chatPanel');
    var closeBtn = document.getElementById('chatClose');
    var backBtn = document.getElementById('chatBack');
    var convList = document.getElementById('chatConversaciones');
    var convView = document.getElementById('chatConversacion');
    var mensajesEl = document.getElementById('chatMensajes');
    var inputEl = document.getElementById('chatInput');
    var sendBtn = document.getElementById('chatSend');
    var badge = document.getElementById('chatBadge');

    var stompClient = null;
    var conversaciones = [];
    var convActualId = null;
    var usuarioId = null;

    function init() {
        if (!toggle) return;

        toggle.addEventListener('click', function () {
            if (panel.classList.contains('d-none')) {
                abrirPanel();
            } else {
                cerrarPanel();
            }
        });

        if (closeBtn) closeBtn.addEventListener('click', cerrarPanel);
        if (backBtn) backBtn.addEventListener('click', mostrarLista);

        inputEl.addEventListener('input', function () {
            sendBtn.disabled = inputEl.value.trim().length === 0;
        });

        inputEl.addEventListener('keydown', function (e) {
            if (e.key === 'Enter' && !sendBtn.disabled) enviarMensaje();
        });

        sendBtn.addEventListener('click', enviarMensaje);

        conectarStomp();
        cargarNoLeidos();
        setInterval(cargarNoLeidos, 30000);
    }

    function abrirPanel() {
        panel.classList.remove('d-none');
        toggle.classList.add('abierto');
        cargarConversaciones();
    }

    function cerrarPanel() {
        panel.classList.add('d-none');
        toggle.classList.remove('abierto');
        convActualId = null;
        convView.classList.add('d-none');
        convList.classList.remove('d-none');
    }

    function mostrarConversacion(convId, nombre) {
        convActualId = convId;
        document.getElementById('chatConvNombre').textContent = nombre;
        convList.classList.add('d-none');
        convView.classList.remove('d-none');
        inputEl.value = '';
        sendBtn.disabled = true;
        inputEl.focus();

        fetch('/api/chat/' + convId + '/mensajes')
            .then(function (r) { return r.json(); })
            .then(function (msgs) {
                renderMensajes(msgs);
                marcarLeidos(convId);
            });

        if (stompClient && stompClient.connected) {
            stompClient.subscribe('/topic/chat.' + convId, function (msg) {
                var m = JSON.parse(msg.body);
                if (m.remitenteId === usuarioId) return;
                agregarMensaje(m);
                marcarLeidos(convId);
            });
        }
    }

    function mostrarLista() {
        convActualId = null;
        convView.classList.add('d-none');
        convList.classList.remove('d-none');
        cargarConversaciones();
    }

    function enviarMensaje() {
        var texto = inputEl.value.trim();
        if (!texto || !convActualId || !stompClient) return;

        var msgLocal = {
            remitenteId: usuarioId,
            contenido: texto,
            fechaEnvio: new Date().toISOString()
        };
        agregarMensaje(msgLocal);

        stompClient.send('/app/chat.enviar', {}, JSON.stringify({
            conversacionId: convActualId,
            contenido: texto
        }));

        inputEl.value = '';
        sendBtn.disabled = true;
    }

    function renderMensajes(msgs) {
        mensajesEl.innerHTML = '';
        if (msgs.length === 0) {
            mensajesEl.innerHTML = '<div class="chat-empty text-center text-muted small py-4">No hay mensajes aún</div>';
            return;
        }
        msgs.forEach(function (m) {
            agregarMensaje(m);
        });
        scrollAbajo();
    }

    function agregarMensaje(m) {
        var div = document.createElement('div');
        div.className = 'chat-msg' + (m.remitenteId === usuarioId ? ' propio' : '');
        div.innerHTML = '<div class="chat-msg-text">' + escapeHtml(m.contenido) + '</div>' +
            '<div class="chat-msg-time">' + formatearHora(m.fechaEnvio) + '</div>';
        mensajesEl.appendChild(div);
        scrollAbajo();
    }

    function scrollAbajo() {
        mensajesEl.scrollTop = mensajesEl.scrollHeight;
    }

    function cargarConversaciones() {
        convList.innerHTML = '<div class="chat-loading text-center py-4 text-muted small"><i class="bi bi-hourglass-split"></i> Cargando...</div>';

        fetch('/api/chat/conversaciones')
            .then(function (r) { return r.json(); })
            .then(function (data) {
                conversaciones = data;
                if (data.length === 0) {
                    convList.innerHTML = '<div class="chat-empty text-center py-4 text-muted small"><i class="bi bi-chat-square-dots d-block fs-3 mb-2"></i>No tienes conversaciones</div>';
                    return;
                }
                convList.innerHTML = '';
                data.forEach(function (c) {
                    var otroNombre = c.estudianteId === usuarioId ? c.tutorNombre : c.estudianteNombre;
                    var item = document.createElement('div');
                    item.className = 'chat-conv-item';
                    item.innerHTML =
                        '<div class="chat-conv-avatar">' + otroNombre.charAt(0).toUpperCase() + '</div>' +
                        '<div class="chat-conv-info">' +
                            '<div class="chat-conv-nombre">' + escapeHtml(otroNombre) + '</div>' +
                            '<div class="chat-conv-preview text-truncate">' + escapeHtml(c.ultimoMensaje || '') + '</div>' +
                        '</div>';
                    item.addEventListener('click', function () {
                        mostrarConversacion(c.id, otroNombre);
                    });
                    convList.appendChild(item);
                });
            })
            .catch(function () {
                convList.innerHTML = '<div class="chat-empty text-center py-4 text-muted small">Error al cargar</div>';
            });
    }

    function marcarLeidos(convId) {
        fetch('/api/chat/' + convId + '/leer', { method: 'POST' }).catch(function () {});
        cargarNoLeidos();
    }

    function cargarNoLeidos() {
        fetch('/api/chat/no-leidas')
            .then(function (r) { return r.json(); })
            .then(function (data) {
                if (data.noLeidas > 0) {
                    badge.textContent = data.noLeidas;
                    badge.classList.remove('d-none');
                } else {
                    badge.classList.add('d-none');
                }
            })
            .catch(function () {});
    }

    function obtenerMiId() {
        return fetch('/api/me')
            .then(function (r) { return r.json(); })
            .then(function (data) {
                if (data.autenticado) {
                    usuarioId = data.id;
                }
            });
    }

    function conectarStomp() {
        obtenerMiId().then(function () {
            var socket = new SockJS('/ws');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function () {
                stompClient.subscribe('/user/queue/conversaciones', function () {
                    if (!panel.classList.contains('d-none') && convActualId === null) {
                        cargarConversaciones();
                    }
                    cargarNoLeidos();
                });
            });
        });
    }

    function formatearHora(fechaStr) {
        var d = new Date(fechaStr);
        return d.toLocaleTimeString('es-EC', { hour: '2-digit', minute: '2-digit' });
    }

    function escapeHtml(text) {
        if (!text) return '';
        var div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    document.addEventListener('DOMContentLoaded', init);
})();
