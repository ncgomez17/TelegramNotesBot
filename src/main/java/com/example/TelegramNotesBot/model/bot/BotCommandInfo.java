package com.example.TelegramNotesBot.model.bot;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BotCommandInfo {
    private String command;
    private String description;
    private String label;
}
