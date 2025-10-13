package com.example.TelegramNotesBot.handlers;

import com.example.TelegramNotesBot.constantes.BotCommandHandler;
import com.example.TelegramNotesBot.services.NasaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class NasaCommandHandler implements BotCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(NasaCommandHandler.class);

    private final NasaService nasaService;

    public NasaCommandHandler(NasaService nasaService) {
        this.nasaService = nasaService;
    }

    @Override
    public Object handle(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String messageText = update.getMessage().getText().trim();

        if (messageText.equalsIgnoreCase("/nasa")) {
            return handleNasaPicture(chatId);
        } else if (messageText.equalsIgnoreCase("/asteroidsNear")) {
            return handleAsteroidsNear(chatId);
        }
        else if(messageText.toLowerCase().startsWith("/solarflares")){
            String[] parts = messageText.split("\\s+");
            int days = 7; // valor por defecto

            if (parts.length > 1) {
                try {
                    days = Integer.parseInt(parts[1]);
                    if (days <= 0) days = 7;
                } catch (NumberFormatException e) {
                    SendMessage msg = new SendMessage(chatId, "⚠️ Uso correcto: /solarFlares [número de días]\nEjemplo: `/solarFlares 3`");
                    msg.setParseMode("Markdown");
                    return msg;
                }
            }
            return handleSolarFlares(days, chatId);
        }
        else if (messageText.toLowerCase().startsWith("/earthevents")) {
            String[] parts = messageText.split("\\s+");
            String category = null;

            if (parts.length > 1) {
                category = parts[1].toLowerCase();
            }

            return handleEarthEvents(category, chatId);
        }
        logger.info("Comando no reconocido.");
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText("Comando no reconocido. Usa /nasa o /asteroidsNear 🚀");
        return msg;
    }

    private SendPhoto handleNasaPicture(String chatId) {
        logger.info("Ejecutando comando /nasa para chatId={}", chatId);

        Map<String, Object> apod = nasaService.getAstronomyPictureOfTheDay();

        String title = (String) apod.get("title");
        String explanation = (String) apod.get("explanation");
        String imageUrl = (String) apod.get("url");

        logger.debug("Datos APOD obtenidos - title: {}, imageUrl: {}", title, imageUrl);

        String caption = "🌌 *" + title + "*\n\n" + explanation;

        if (caption.length() > 1024) {
            caption = caption.substring(0, 1020) + "...";
            logger.warn("Caption recortado a 1024 caracteres para chatId={}", chatId);
        }

        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(imageUrl));
        photo.setCaption(caption);
        photo.setParseMode("Markdown");

        return photo;
    }

    private SendMessage handleAsteroidsNear(String chatId) {
        logger.info("Ejecutando comando /asteroidsNear para chatId={}", chatId);

        String today = LocalDate.now().toString();
        Map<String, Object> response = nasaService.getNearEarthObjects(today, null);

        Map<String, Object> nearEarthObjects = (Map<String, Object>) response.get("near_earth_objects");
        List<Map<String, Object>> asteroidsToday = (List<Map<String, Object>>) nearEarthObjects.get(today);

        if (asteroidsToday == null || asteroidsToday.isEmpty()) {
            SendMessage msg = new SendMessage(chatId, "☄️ No se detectaron asteroides cercanos hoy.");
            return msg;
        }

        StringBuilder message = new StringBuilder("🪐 *Asteroides cercanos a la Tierra hoy (" + today + ")*\n\n");
        logger.info("Obteniendo datos de los asteroides del comando /asteroidsNear para chatId={}", chatId);

        int count = 0;
        for (Map<String, Object> asteroid : asteroidsToday) {
            if (count >= 5) break;

            String name = (String) asteroid.get("name");

            Map<String, Object> closeApproachData = ((List<Map<String, Object>>) asteroid.get("close_approach_data")).get(0);
            Map<String, Object> missDistance = (Map<String, Object>) closeApproachData.get("miss_distance");
            Map<String, Object> velocity = (Map<String, Object>) closeApproachData.get("relative_velocity");

            String distanceKm = String.format("%,.0f", Double.parseDouble((String) missDistance.get("kilometers")));
            String velocityKmH = String.format("%,.0f", Double.parseDouble((String) velocity.get("kilometers_per_hour")));

            message.append("☄️ *").append(name).append("*\n")
                    .append("Distancia: ").append(distanceKm).append(" km\n")
                    .append("Velocidad: ").append(velocityKmH).append(" km/h\n\n");

            count++;
        }

        message.append("_Fuente: NASA NeoWs API_");

        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(message.toString());
        msg.setParseMode("Markdown");
        return msg;
    }

    public SendMessage handleSolarFlares(Integer days, String chatId) {
        logger.info("Ejecutando comando /solarFlares");
        List<Map<String, Object>> flares = nasaService.getRecentSolarFlares(7); // últimos 7 días

        if (flares.isEmpty()) {
            return new SendMessage(chatId, "☀️ No se registraron erupciones solares recientes.");
        }

        StringBuilder message = new StringBuilder("🔥 *Últimas erupciones solares detectadas:*\n\n");

        int count = 0;
        for (Map<String, Object> flare : flares) {
            if (count >= 5) break; // mostrar solo las 5 más recientes

            String classType = (String) flare.get("classType");
            String beginTime = (String) flare.get("beginTime");
            String peakTime = (String) flare.get("peakTime");
            String sourceLocation = (String) flare.get("sourceLocation");
            Object region = flare.get("activeRegionNum");

            message.append("☀️ *Clase:* ").append(classType).append("\n")
                    .append("Inicio: ").append(beginTime).append("\n")
                    .append("Pico: ").append(peakTime).append("\n")
                    .append("Ubicación solar: ").append(sourceLocation != null ? sourceLocation : "Desconocida").append("\n")
                    .append("Región activa: ").append(region != null ? region : "N/A").append("\n\n");

            count++;
        }

        message.append("_Fuente: NASA DONKI API_");

        SendMessage msg = new SendMessage(chatId, message.toString());
        msg.setParseMode("Markdown");
        return msg;
    }

    private SendMessage handleEarthEvents(String category, String chatId) {
        logger.info("Ejecutando comando /earthEvents para chatId={} con categoría={}", chatId, category);
        List<Map<String, Object>> events = nasaService.getEarthEvents(category);

        if (events.isEmpty()) {
            return new SendMessage(chatId, "🌎 No se encontraron eventos de tipo '" + category + "' en los últimos " + " días.");
        }

        StringBuilder message = new StringBuilder("🌎 *Eventos naturales recientes* (" + category + ", últimos " + " días):\n\n");

        int count = 0;
        for (Map<String, Object> event : events) {
            if (count >= 5) break; // mostrar solo los 5 más recientes

            String title = (String) event.get("title");
            Object id = event.get("id");
            List<Map<String, Object>> geometries = (List<Map<String, Object>>) event.get("geometry");
            String date = geometries != null && !geometries.isEmpty() ? (String) geometries.get(0).get("date") : "Desconocida";

            message.append("• *").append(title).append("*\n")
                    .append("Fecha: ").append(date).append("\n")
                    .append("ID: ").append(id).append("\n\n");

            count++;
        }

        message.append("_Fuente: NASA EONET API_");

        SendMessage msg = new SendMessage(chatId, message.toString());
        msg.setParseMode("Markdown");
        return msg;
    }

}
