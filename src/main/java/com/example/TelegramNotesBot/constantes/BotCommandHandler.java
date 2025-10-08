package com.example.TelegramNotesBot.constantes;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotCommandHandler {
    Object handle(Update update) throws Exception;
}
