package com.example.TelegramNotesBot.services;

import com.example.TelegramNotesBot.constantes.BotCommandHandler;
import com.example.TelegramNotesBot.handlers.NasaCommandHandler;
import com.example.TelegramNotesBot.handlers.PlanetsCommandHandler;
import com.example.TelegramNotesBot.handlers.StartCommandHandler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BotCommandRegistry {

    private final Map<String, BotCommandHandler> commands = new HashMap<>();

    public BotCommandRegistry(
            StartCommandHandler startHandler,
            NasaCommandHandler nasaHandler,
            PlanetsCommandHandler planetsHandler
    ) {
        commands.put("/start", startHandler);
        commands.put("/nasa", nasaHandler);
        commands.put("/planetas", planetsHandler);
    }

    public BotCommandHandler getHandler(String command) {
        return commands.get(command.toLowerCase());
    }
}

