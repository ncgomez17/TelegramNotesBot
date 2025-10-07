package com.example.TelegramNotesBot.constantes;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotCommandHandler {
    PartialBotApiMethod<?> handle(Update update) throws Exception;
}
