package com.example.TelegramNotesBot.services;

import com.example.TelegramNotesBot.model.bot.astronomy.AstronomyProperties;
import com.example.TelegramNotesBot.model.bot.astronomy.VisiblePlanetsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class StellariumService {

    private final AstronomyProperties properties;
    private final WebClient webClient;

    public StellariumService(AstronomyProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getStellarium().getBaseUrl())
                .build();
    }
    public List<String> getVisiblePlanetsTonight() {
        String url = "/api/planets/visible?date=" + LocalDate.now();

        VisiblePlanetsResponse response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(VisiblePlanetsResponse.class)
                .block();

        if (response != null && response.getPlanets() != null) {
            return response.getPlanets();
        } else {
            return Arrays.asList("Mercurio", "Venus", "Marte", "Júpiter", "Saturno");
        }
    }
}

