package com.example.TelegramNotesBot.bot;

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
    public TelegramBot telegramBot(BotProperties botProperties, DefaultBotOptions options) {
        return new TelegramBot(botProperties, options);
    }
}

