package com.example.TelegramNotesBot.handlers;

import com.example.TelegramNotesBot.services.TelegramBot;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequestMapping("/webhook")
public class TelegramWebhookController {

    private final TelegramBot telegramBot;

    public TelegramWebhookController(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostMapping
    public ResponseEntity<?> onUpdateReceived(@RequestBody Update update) {
        telegramBot.onWebhookUpdateReceived(update);
        return ResponseEntity.ok().build();
    }
}
