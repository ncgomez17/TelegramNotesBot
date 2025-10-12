package com.example.TelegramNotesBot.services;


import com.example.TelegramNotesBot.model.bot.astronomy.AstronomyProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
                .block();
    }

    /**
     * Obtiene las erupciones solares (FLR) entre dos fechas.
     */

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
                .block();
    }


    /**
     * Obtiene las erupciones solares de los últimos N días (por ejemplo, 7 días).
     */
    public List<Map<String, Object>> getRecentSolarFlares(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        return getSolarFlares(startDate.toString(), endDate.toString());
    }

    /**
     * Obtiene eventos naturales recientes de la NASA EONET.
     * @param category Tipo de evento (volcanoes, wildfires, earthquakes, all)
     * @param days Cantidad de días hacia atrás
     * @return Lista de eventos como Map<String, Object>
     */
    public List<Map<String, Object>> getEarthEvents(String category, int days) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("eonet.gsfc.nasa.gov")
                        .path("/api/v3/events")
                        .queryParam("category", category)
                        .queryParam("days", days)
                        .queryParam("status", "open")
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> (List<Map<String, Object>>) response.get("events"))
                .blockOptional()
                .orElse(List.of());
    }

    /**
     * Sobrecarga: por defecto últimos 7 días y categoría "all"
     */
    public List<Map<String, Object>> getEarthEvents(String category) {
        return getEarthEvents(category, 7);
    }

    public List<Map<String, Object>> getEarthEvents() {
        return getEarthEvents("all", 7);
    }
}
