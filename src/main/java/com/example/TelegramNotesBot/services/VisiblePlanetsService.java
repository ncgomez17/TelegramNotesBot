package com.example.TelegramNotesBot.services;

import com.example.TelegramNotesBot.model.bot.astronomy.AstronomyProperties;
import com.example.TelegramNotesBot.model.bot.astronomy.VisiblePlanetsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class VisiblePlanetsService {

    private final AstronomyProperties properties;
    private final WebClient webClient;

    public VisiblePlanetsService(AstronomyProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getVisiblePlanets().getBaseUrl())
                .build();
    }
    public List<String> getVisiblePlanetsTonight(double latitude, double longitude) {
        String url = String.format("/v3?latitude=%s&longitude=%s", latitude, longitude);

        try {
            Mono<Map> responseMono = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class);

            Map<String, Object> response = responseMono.block();

            if (response != null && response.containsKey("planets")) {
                return (List<String>) response.get("planets");
            } else {
                return Arrays.asList("Mercurio", "Venus", "Marte", "Júpiter", "Saturno");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Arrays.asList("Mercurio", "Venus", "Marte", "Júpiter", "Saturno");
        }
    }

}

