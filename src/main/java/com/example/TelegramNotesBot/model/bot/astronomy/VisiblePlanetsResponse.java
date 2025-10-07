package com.example.TelegramNotesBot.model.bot.astronomy;

import lombok.Data;
import java.util.List;

@Data
public class VisiblePlanetsResponse {
    private String date;
    private List<String> planets;
}
