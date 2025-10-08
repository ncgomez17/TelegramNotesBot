package com.example.TelegramNotesBot.handlers;

import com.example.TelegramNotesBot.constantes.BotCommandHandler;
import com.example.TelegramNotesBot.services.NasaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Component
public class NasaCommandHandler implements BotCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(NasaCommandHandler.class);

    private final NasaService nasaService;

    public NasaCommandHandler(NasaService nasaService) {
        this.nasaService = nasaService;
    }

    @Override
    public SendPhoto handle(Update update){
        String chatId = update.getMessage().getChatId().toString();
        logger.info("Ejecutando comando /nasa para chatId={}", chatId);

        Map<String, Object> apod = nasaService.getAstronomyPictureOfTheDay();

        String title = (String) apod.get("title");
        String explanation = (String) apod.get("explanation");
        String imageUrl = (String) apod.get("url");

        logger.debug("Datos APOD obtenidos - title: {}, imageUrl: {}", title, imageUrl);

        String caption = "🌌 *" + title + "*\n\n" + explanation;

        // Recortar caption si es demasiado largo
        if (caption.length() > 1024) {
            caption = caption.substring(0, 1020) + "...";
            logger.warn("Caption recortado a 1024 caracteres para chatId={}", chatId);
        }

        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(imageUrl));
        photo.setCaption(caption);
        photo.setParseMode("Markdown");

        logger.info("Preparado SendPhoto para chatId={}", chatId);
        return photo;
    }
}
