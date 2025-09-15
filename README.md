## Configuración inicial del Bot de Telegram

Este bot está diseñado para funcionar mediante **webhook**, lo que significa que Telegram enviará los mensajes directamente a nuestro servidor, y el bot responderá en tiempo real. Esta es la configuración básica; más adelante se irán agregando funcionalidades y comandos adicionales.

### Paso 1: Setear el Webhook

Debemos indicarle a Telegram dónde debe enviar los mensajes del bot. Para ello, ejecutamos el siguiente comando en la terminal (Linux/macOS/Windows con Git Bash o PowerShell):

```bash
curl -X POST "https://api.telegram.org/bot<TU_TOKEN_BOT>/setWebhook?url=<URL_SERVIDOR>/webhook&max_connections=40&drop_pending_updates=true"

