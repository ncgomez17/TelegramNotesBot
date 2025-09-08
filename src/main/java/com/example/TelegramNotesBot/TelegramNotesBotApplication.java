package com.example.TelegramNotesBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TelegramNotesBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramNotesBotApplication.class, args);
		System.out.println("Bot de Telegram iniciado y escuchando mensajes...");
	}

}
