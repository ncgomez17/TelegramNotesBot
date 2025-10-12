package com.example.TelegramNotesBot.handlers;


import com.example.TelegramNotesBot.constantes.BotCommandHandler;
import com.example.TelegramNotesBot.services.BotCommandRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class HelpCommandHandler implements BotCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(HelpCommandHandler.class);

    private final BotCommandRegistry commandRegistry;

    public HelpCommandHandler(@Lazy BotCommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @Override
    public SendMessage handle(Update update) {
        String chatId = update.getMessage().getChatId().toString();

        logger.info("Ejecutando comando /help para chatId={}", chatId);
        StringBuilder message = new StringBuilder("🆘 *Lista de comandos disponibles:*\n\n");

        // Recorremos todos los comandos registrados
        for (String command : commandRegistry.getAllCommands().keySet()) {
            switch (command) {
                case "/start" -> message.append("/start - Saludo inicial del bot\n");
                case "/nasa" -> message.append("/nasa - Astronomy Picture of the Day\n");
                case "/solarFlares" -> message.append("/solarFlares [días] - Últimas erupciones solares (por defecto 7 días)\n");
                case "/asteroidsNear" -> message.append("/asteroidsNear - Asteroides cercanos hoy\n");
                case "/earthEvents" -> message.append("/earthEvents [tipo] [días] - Eventos naturales recientes (volcanes, incendios, terremotos)\n");
                case "/planetas" -> message.append("/planetas - Información de planetas\n");
                default -> message.append(command).append("\n");
            }
        }

        SendMessage msg = new SendMessage(chatId, message.toString());
        msg.setParseMode("Markdown");
        logger.info("Mensaje de /help preparado para chatId={}", chatId);
        return msg;
    }
}

