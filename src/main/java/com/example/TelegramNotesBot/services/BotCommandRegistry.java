package com.example.TelegramNotesBot.services;

import com.example.TelegramNotesBot.constantes.BotCommandHandler;
import com.example.TelegramNotesBot.handlers.HelpCommandHandler;
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
            PlanetsCommandHandler planetsHandler,
            HelpCommandHandler helpCommandHandler
    ) {
        commands.put("/start".toLowerCase(), startHandler);
        commands.put("/nasa".toLowerCase(), nasaHandler);
        commands.put("/solarFlares".toLowerCase(), nasaHandler);
        commands.put("/asteroidsNear".toLowerCase(), nasaHandler);
        commands.put("/earthEvents".toLowerCase(), nasaHandler);
        commands.put("/planetas".toLowerCase(), planetsHandler);
        commands.put("/help".toLowerCase(), helpCommandHandler);
    }

    public BotCommandHandler getHandler(String command) {
        return commands.get(command.toLowerCase());
    }

    public Map<String, BotCommandHandler> getAllCommands() {
        return Map.copyOf(commands);
    }
}

