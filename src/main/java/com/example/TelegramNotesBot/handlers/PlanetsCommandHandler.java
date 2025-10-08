package com.example.TelegramNotesBot.handlers;

import com.example.TelegramNotesBot.constantes.BotCommandHandler;
import com.example.TelegramNotesBot.services.VisiblePlanetsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Arrays;
import java.util.List;

@Component
public class PlanetsCommandHandler implements BotCommandHandler {

    private final VisiblePlanetsService visiblePlanetsService;

    public PlanetsCommandHandler(VisiblePlanetsService visiblePlanetsService) {
        this.visiblePlanetsService= visiblePlanetsService;
    }

    @Override
    public SendMessage handle(Update update) {
        String chatId = update.getMessage().getChatId().toString();

        if (update.getMessage().hasLocation()) {
            double latitude = update.getMessage().getLocation().getLatitude();
            double longitude = update.getMessage().getLocation().getLongitude();
            List<String> planets = visiblePlanetsService.getVisiblePlanetsTonight(latitude, longitude);

            String message = planets.isEmpty()
                    ? "🔭 No hay planetas visibles esta noche."
                    : "🔭 Planetas visibles esta noche:\n" + String.join(", ", planets);
            return new SendMessage(chatId, message);

        } else {
            // Si no hay ubicación, pedimos al usuario que la envíe
            SendMessage msg = new SendMessage();
            msg.setChatId(chatId);
            msg.setText("Por favor, envíame tu ubicación para calcular los planetas visibles.");

            ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
            KeyboardButton locationButton = new KeyboardButton("📍 Enviar ubicación");
            locationButton.setRequestLocation(true);
            KeyboardRow row = new KeyboardRow();
            row.add(locationButton);
            keyboard.setKeyboard(List.of(row));
            keyboard.setResizeKeyboard(true);
            keyboard.setOneTimeKeyboard(true);
            msg.setReplyMarkup(keyboard);

            return msg;
        }
    }


}

