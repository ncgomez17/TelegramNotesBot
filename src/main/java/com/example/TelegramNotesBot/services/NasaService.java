package com.example.TelegramNotesBot.services;


import com.example.TelegramNotesBot.model.bot.astronomy.AstronomyProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class NasaService {

    private final AstronomyProperties properties;
    private final WebClient webClient;

    private static final Duration TIMEOUT = Duration.ofSeconds(10); // Timeout global de 10 segundos

    public NasaService(AstronomyProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getNasa().getBaseUrl())
                .build();
    }

    // -----------------------------
    // 1️⃣ Astronomy Picture of the Day
    // -----------------------------
    public Map<String, Object> getAstronomyPictureOfTheDay() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/planetary/apod")
                        .queryParam("api_key", properties.getNasa().getApiKey())
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(TIMEOUT)
                .onErrorResume(throwable -> fallbackApod())
                .block();
    }

    private Mono<Map<String, Object>> fallbackApod() {
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("title", "No disponible");
        fallback.put("explanation", "La API de NASA no respondió a tiempo.");
        fallback.put("url", "");
        return Mono.just(fallback);
    }

    // -----------------------------
    // 2️⃣ Near Earth Objects
    // -----------------------------
    public Map<String, Object> getNearEarthObjects(String startDate, String endDate) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/neo/rest/v1/feed")
                        .queryParam("start_date", startDate)
                        .queryParamIfPresent("end_date", Optional.ofNullable(endDate))
                        .queryParam("api_key", properties.getNasa().getApiKey())
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(TIMEOUT)
                .onErrorResume(throwable -> fallbackMap())
                .block();
    }

    private Mono<Map<String, Object>> fallbackMap() {
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("message", "No disponible");
        return Mono.just(fallback);
    }

    // -----------------------------
    // 3️⃣ Solar Flares entre fechas
    // -----------------------------
    public List<Map<String, Object>> getSolarFlares(String startDate, String endDate) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/DONKI/FLR")
                        .queryParam("startDate", startDate)
                        .queryParam("endDate", endDate)
                        .queryParam("api_key", properties.getNasa().getApiKey())
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .timeout(TIMEOUT)
                .onErrorResume(throwable -> Mono.just(List.of()))
                .block();
    }

    // -----------------------------
    // 4️⃣ Solar Flares recientes (últimos N días)
    // -----------------------------
    public List<Map<String, Object>> getRecentSolarFlares(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        return getSolarFlares(startDate.toString(), endDate.toString());
    }

    // -----------------------------
    // 5️⃣ Eventos terrestres recientes (NASA EONET)
    // -----------------------------
    public List<Map<String, Object>> getEarthEvents(String category) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(7);

            return webClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder
                                .scheme("https")
                                .host("eonet.gsfc.nasa.gov")
                                .path("/api/v3/events")
                                .queryParam("status", "open")
                                .queryParam("limit", 20)
                                .queryParam("start", startDate)
                                .queryParam("end", endDate);

                        if (category != null && !category.isBlank()) {
                            builder.queryParam("category", category);
                        }

                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(TIMEOUT)
                    .onErrorResume(throwable -> Mono.just(Map.of("events", List.of())))
                    .map(response -> (List<Map<String, Object>>) response.get("events"))
                    .blockOptional()
                    .orElse(List.of());

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

}
