package com.example.TelegramNotesBot.services;

import com.example.TelegramNotesBot.model.bot.astronomy.AstronomyProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class VisiblePlanetsService {

    private static final Logger logger = LoggerFactory.getLogger(VisiblePlanetsService.class);

    private final AstronomyProperties properties;
    private final WebClient webClient;
    private static final Duration TIMEOUT = Duration.ofSeconds(10); // Timeout global

    public VisiblePlanetsService(AstronomyProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getVisiblePlanets().getBaseUrl())
                .build();
    }

    public List<String> getVisiblePlanetsTonight(double latitude, double longitude) {
        String url = String.format("/v3?latitude=%s&longitude=%s", latitude, longitude);
        logger.info("Solicitando planetas visibles para lat={}, lon={} con URL={}", latitude, longitude, url);

        try {
            Mono<Map> responseMono = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(TIMEOUT)
                    .onErrorResume(throwable -> {
                        logger.error("Error al obtener planetas visibles o timeout excedido", throwable);
                        return Mono.just(Map.of("planets", defaultPlanets()));
                    });

            Map<String, Object> response = responseMono.block();

            if (response != null && response.containsKey("planets")) {
                List<String> planetas = (List<String>) response.get("planets");
                logger.info("Planetas obtenidos: {}", planetas);
                return planetas;
            } else {
                logger.warn("No se encontraron planetas en la respuesta. Devolviendo lista por defecto.");
                return defaultPlanets();
            }

        } catch (Exception e) {
            logger.error("Error inesperado al obtener planetas visibles", e);
            return defaultPlanets();
        }
    }

    private List<String> defaultPlanets() {
        return Arrays.asList("Mercurio", "Venus", "Marte", "Júpiter", "Saturno");
    }
}
