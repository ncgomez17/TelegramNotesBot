package com.example.TelegramNotesBot.config;

import com.example.TelegramNotesBot.model.bot.BotProperties;
import com.example.TelegramNotesBot.services.NasaService;
import com.example.TelegramNotesBot.services.StellariumService;
import com.example.TelegramNotesBot.services.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Configuration
public class TelegramBotConfig {

    @Bean
    public DefaultBotOptions defaultBotOptions() {
        return new DefaultBotOptions();
    }

    @Bean
    public TelegramBot telegramBot(BotProperties botProperties, NasaService nasaService, StellariumService stellariumService, DefaultBotOptions options) {
        return new TelegramBot(botProperties, nasaService,stellariumService, options);
    }
}

