package com.example.TelegramNotesBot.services;

import com.example.TelegramNotesBot.model.bot.BotProperties;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

@Service
public class TelegramBot extends TelegramWebhookBot {

    private final BotProperties botProperties;
    private final NasaService nasaService;

    public TelegramBot(BotProperties botProperties, NasaService nasaService, DefaultBotOptions options) {
        super(options);
        this.botProperties = botProperties;
        this.nasaService = nasaService;
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getApiKey();
    }

    @Override
    public String getBotPath() {
        return botProperties.getWebhookPath();
    }

    @Override
    public SendMessage onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText().toLowerCase();

            try {
                switch (messageText) {
                    case "/start":
                        return new SendMessage(chatId, "👋 ¡Hola! Soy tu bot astronómico.\nUsa /nasa para ver la imagen del día 🚀");
                    case "/nasa":
                        Map<String, Object> apod = nasaService.getAstronomyPictureOfTheDay();
                        String title = (String) apod.get("title");
                        String explanation = (String) apod.get("explanation");
                        String imageUrl = (String) apod.get("url");

                        SendPhoto photo = new SendPhoto();
                        photo.setChatId(chatId);
                        photo.setPhoto(new org.telegram.telegrambots.meta.api.objects.InputFile(imageUrl));
                        photo.setCaption("🌌 *" + title + "*\n\n" + explanation);
                        photo.setParseMode("Markdown");

                        execute(photo);
                        return null;
                    default:
                        return new SendMessage(chatId, "❓ Comando no reconocido. Usa /nasa o /start");
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
