package com.example.TelegramNotesBot.model.bot.astronomy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "astronomy")
@Getter
@Setter
public class AstronomyProperties {

    private VisiblePlanets visiblePlanets= new VisiblePlanets();
    private Nasa nasa = new Nasa();
    private Iss iss = new Iss();

    @Getter @Setter
    public static class VisiblePlanets {
        private String baseUrl;
    }

    @Getter @Setter
    public static class Nasa {
        private String baseUrl;
        private String apiKey;
    }

    @Getter @Setter
    public static class Iss {
        private String baseUrl;
    }
}
