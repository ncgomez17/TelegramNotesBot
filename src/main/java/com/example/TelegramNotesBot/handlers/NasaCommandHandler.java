package com.example.TelegramNotesBot.handlers;

import com.example.TelegramNotesBot.constantes.BotCommandHandler;
import com.example.TelegramNotesBot.services.NasaService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.Map;

@Component
public class NasaCommandHandler implements BotCommandHandler {

    private final NasaService nasaService;

    public NasaCommandHandler(NasaService nasaService) {
        this.nasaService = nasaService;
    }

    @Override
    public SendPhoto handle(Update update) throws Exception {
        String chatId = update.getMessage().getChatId().toString();
        Map<String, Object> apod = nasaService.getAstronomyPictureOfTheDay();

        String title = (String) apod.get("title");
        String explanation = (String) apod.get("explanation");
        String imageUrl = (String) apod.get("url");
        System.out.println(title);
        System.out.println(explanation);
        System.out.println(imageUrl);

        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(imageUrl));
        photo.setCaption("🌌 *" + title + "*\n\n" + explanation);
        photo.setParseMode("Markdown");

        return photo;
    }

}

