package com.example.TelegramNotesBot.model.bot;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("bot")
public class BotProperties {

    private String apiKey;
    private String username;
    private String webhookPath;

}