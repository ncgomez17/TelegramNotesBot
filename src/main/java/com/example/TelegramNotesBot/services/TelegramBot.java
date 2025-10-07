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
        System.out.println("Entra por el manejador de comandos");
        if (update.hasMessage() && update.getMessage().hasText()) {
            String rawText = update.getMessage().getText();
            String messageText = rawText.split(" ")[0].split("@")[0].toLowerCase();

            BotCommandHandler handler = commandRegistry.getHandler(messageText);
            if (handler != null) {
                try {
                    PartialBotApiMethod<?> response = handler.handle(update);
                    if (response instanceof BotApiMethod<?>) {
                        return (BotApiMethod<?>) response;
                    } else {
                        return new SendMessage(update.getMessage().getChatId().toString(),
                                "❌ Error inesperado en el comando");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return new SendMessage(update.getMessage().getChatId().toString(), "❌ Error al procesar el comando");
                }
            } else {
                return new SendMessage(update.getMessage().getChatId().toString(),
                        "❓ Comando no reconocido. Usa /nasa, /planetas o /start");
            }
        }
        return null;
    }


}
