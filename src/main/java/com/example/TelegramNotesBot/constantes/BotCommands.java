package com.example.TelegramNotesBot.constantes;

import com.example.TelegramNotesBot.bot.BotCommandInfo;

import java.util.List;

public class BotCommands {
    public static final BotCommandInfo START = new BotCommandInfo("/start", "Greets and recommends help command", "Start");
    public static final BotCommandInfo HELLO = new BotCommandInfo("/hello", "Simple greeter", "Hello");
    public static final BotCommandInfo HELP = new BotCommandInfo("/help", "Shows available commands", "Help");
    // etc...

    public static final List<BotCommandInfo> ALL = List.of(START, HELLO, HELP);
}
