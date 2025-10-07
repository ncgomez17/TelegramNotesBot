package com.example.TelegramNotesBot.services;


import com.example.TelegramNotesBot.model.bot.astronomy.AstronomyProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class NasaService {

    private final AstronomyProperties properties;
    private final WebClient webClient;

    public NasaService(AstronomyProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getNasa().getBaseUrl())
                .build();
    }

    public Map<String, Object> getAstronomyPictureOfTheDay() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/planetary/apod")
                        .queryParam("api_key", properties.getNasa().getApiKey())
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}
