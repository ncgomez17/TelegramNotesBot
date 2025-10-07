package com.example.TelegramNotesBot.handlers;

import com.example.TelegramNotesBot.constantes.BotCommandHandler;
import com.example.TelegramNotesBot.services.StellariumService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class PlanetsCommandHandler implements BotCommandHandler {

    private final StellariumService stellariumService;

    public PlanetsCommandHandler(StellariumService stellariumService) {
        this.stellariumService = stellariumService;
    }

    @Override
    public SendMessage handle(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        List<String> planets = stellariumService.getVisiblePlanetsTonight();
        return new SendMessage(chatId, "🔭 Planetas visibles esta noche:\n" + String.join(", ", planets));
    }
}

