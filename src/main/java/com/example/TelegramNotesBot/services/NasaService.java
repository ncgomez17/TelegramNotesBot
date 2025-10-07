package com.example.TelegramNotesBot.services;


import com.example.TelegramNotesBot.model.bot.astronomy.AstronomyProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class NasaService {

    private final AstronomyProperties properties;
    private final RestTemplate restTemplate;

    public NasaService(AstronomyProperties properties) {
        this.properties = properties;
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> getAstronomyPictureOfTheDay() {
        String url = UriComponentsBuilder
                .fromHttpUrl(properties.getNasa().getBaseUrl() + "/planetary/apod")
                .queryParam("api_key", properties.getNasa().getApiKey())
                .toUriString();

        return restTemplate.getForObject(url, Map.class);
    }
}
