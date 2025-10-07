package com.example.TelegramNotesBot.handlers;

import com.example.TelegramNotesBot.constantes.BotCommandHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartCommandHandler implements BotCommandHandler {

    @Override
    public SendMessage handle(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        return new SendMessage(chatId, "👋 ¡Hola! Soy el bot multiusos de Nicolás.\nDe momento tenemos unas pocas funciones astronómicas.");
    }
}

