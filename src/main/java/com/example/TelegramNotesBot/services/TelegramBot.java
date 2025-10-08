package com.example.TelegramNotesBot.services;

import com.example.TelegramNotesBot.constantes.BotCommandHandler;
import com.example.TelegramNotesBot.model.bot.BotProperties;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;

@Service
public class TelegramBot extends TelegramWebhookBot {

    private final BotProperties botProperties;
    private final BotCommandRegistry commandRegistry;

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
        if (!update.hasMessage()) return null;

        BotCommandHandler handler = null;
        if (update.getMessage().hasText()) {
            String messageText = update.getMessage().getText().split(" ")[0].split("@")[0].toLowerCase();
            handler = commandRegistry.getHandler(messageText);
        } else if (update.getMessage().hasLocation()) {
            // Usamos el mismo handler de /planetas
            handler = commandRegistry.getHandler("/planetas");
        }


        if (handler == null) {
            try {
                execute(new SendMessage(update.getMessage().getChatId().toString(),
                        "❓ Comando no reconocido. Usa /start, /planetas o /nasa"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return null;
        }

        try {
            Object response = handler.handle(update); // puede ser SendMessage, SendPhoto, etc.

            if (response instanceof SendMessage) {
                execute((SendMessage) response);
            } else if (response instanceof SendPhoto) {
                execute((SendPhoto) response);
            } else {
                System.err.println("⚠️ Tipo de respuesta no manejado: " + response.getClass());
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                execute(new SendMessage(update.getMessage().getChatId().toString(),
                        "❌ Error al procesar el comando"));
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }

        // Siempre devolvemos null porque ya ejecutamos el mensaje
        return null;
    }



}
