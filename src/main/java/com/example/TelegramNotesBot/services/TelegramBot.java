package com.example.TelegramNotesBot.services;

import com.example.TelegramNotesBot.constantes.BotCommandHandler;
import com.example.TelegramNotesBot.model.bot.BotProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Service
public class TelegramBot extends TelegramWebhookBot {

    private final BotProperties botProperties;
    private final BotCommandRegistry commandRegistry;

    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    public TelegramBot(BotProperties botProperties,
                       BotCommandRegistry commandRegistry,
                       DefaultBotOptions options) {
        super(options);
        this.botProperties = botProperties;
        this.commandRegistry = commandRegistry;
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getApiKey();
    }

    @Override
    public String getBotPath() {
        return botProperties.getWebhookPath();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            logger.warn("Update recibido sin mensaje, ignorando");
            return null;
        }

        BotCommandHandler handler = null;
        String chatId = update.getMessage().getChatId().toString();

        if (update.getMessage().hasText()) {
            String messageText = update.getMessage().getText().split(" ")[0].split("@")[0].toLowerCase();
            handler = commandRegistry.getHandler(messageText);
            logger.info("Comando recibido: '{}' en chatId={}", messageText, chatId);
        } else if (update.getMessage().hasLocation()) {
            // Usamos el mismo handler de /planetas
            handler = commandRegistry.getHandler("/planetas");
            logger.info("Ubicación recibida en chatId={}", chatId);
        }

        if (handler == null) {
            logger.warn("Comando no reconocido en chatId={}", chatId);
            try {
                execute(new SendMessage(chatId, "❓ Comando no reconocido. Usa /start, /planetas o /nasa"));
            } catch (TelegramApiException e) {
                logger.error("Error al enviar mensaje de comando no reconocido", e);
            }
            return null;
        }

        try {
            Object response = handler.handle(update); // puede ser SendMessage, SendPhoto, etc.

            if (response instanceof SendMessage sendMessage) {
                logger.info("Enviando SendMessage a chatId={}", chatId);
                execute(sendMessage);
            } else if (response instanceof SendPhoto sendPhoto) {
                logger.info("Enviando SendPhoto a chatId={}", chatId);
                execute(sendPhoto);
            } else {
                logger.warn("Tipo de respuesta no manejado: {} en chatId={}", response.getClass(), chatId);
            }

        } catch (Exception e) {
            logger.error("Error al procesar comando en chatId={}", chatId, e);
            try {
                execute(new SendMessage(chatId, "❌ Error al procesar el comando"));
            } catch (TelegramApiException ex) {
                logger.error("Error al enviar mensaje de error", ex);
            }
        }
        return null;
    }



}
