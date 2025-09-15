package com.example.TelegramNotesBot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.TelegramNotesBot.constantes.BotCommands;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class TelegramBot extends TelegramWebhookBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    private final BotProperties botProperties;

    public TelegramBot(BotProperties botProperties,  DefaultBotOptions options) {
        super(options, botProperties.getApiKey());
        this.botProperties = botProperties;

        try {
            List<BotCommand> commands = BotCommands.ALL.stream()
                    .map(info -> new BotCommand(info.getCommand(), info.getDescription()))
                    .toList();
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            logger.error("Failed to set bot commands: {}", e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getApiKey();
    }
    @Override
    public String getBotPath() {
        return "/webhook";
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String receivedMessage = update.getMessage().getText();

            String username = String.format("%s %s",
                    update.getMessage().getFrom().getFirstName(),
                    update.getMessage().getFrom().getLastName());

            handleCommands(chatId, receivedMessage, username);
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
        return null;
    }

    private void handleCommands(String chatId, String receivedMessage, String username) {

        String messageText = receivedMessage.trim();

        Optional<String> optionalCommand = BotCommands.ALL.stream()
                .filter(info -> info.getLabel().equalsIgnoreCase(messageText))
                .map(BotCommandInfo::getCommand)
                .findFirst();

        String commandKey;

        if (optionalCommand.isPresent()) {
            commandKey = optionalCommand.get().toLowerCase();
        } else if (messageText.startsWith("/")) {
            commandKey = messageText.split(" ")[0].toLowerCase();
        } else {
            commandKey = messageText.toLowerCase();
        }

        Map<String, Consumer<String>> commandHandlers = Map.of(
                "/start", id -> handleStartCommand(id, username)
/*                "/start_with_reply", () -> handleStartWithReplyCommand(chatId, username),
                "/hello", () -> handleHelloCommand(chatId, username),
                "/joke", () -> handleJokeCommand(chatId),
                "/name_surname", () -> handleNameSurnameCommand(chatId, username),
                "/help", () -> handleHelpCommand(chatId),
                "/chat_id", () -> sendMessage(chatId, chatId)*/
        );

        commandHandlers.getOrDefault(commandKey, id -> handleUnknownCommand(id, username))
                .accept(chatId);
    }

    private void handleStartCommand(String chatId, String username) {
        String message = String.format("Hello %s! Welcome to the Telegram Bot. Choose an option:", username);
        sendMessage(chatId, message, getInlineKeyboard());
    }

    private void sendMessage(String chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(keyboard);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message: {}", e.getMessage());
        }
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message: {}", e.getMessage());
        }
    }

    private InlineKeyboardMarkup getInlineKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Option 1");
        button1.setCallbackData("OPTION_1");

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Option 2");
        button2.setCallbackData("OPTION_2");

        keyboard.setKeyboard(List.of(List.of(button1, button2)));
        return keyboard;
    }

    private void handleUnknownCommand(String chatId, String username) {
        String message = String.format("❌ Sorry %s, I don't recognize that command. Try /help to see available commands.", username);
        sendMessage(chatId, message);
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String username = String.format("%s %s",
                callbackQuery.getFrom().getFirstName(),
                callbackQuery.getFrom().getLastName());

        switch (callbackData) {
            case "OPTION_1":
                sendMessage(chatId, String.format("✅ %s, you selected Option 1!", username));
                break;

            case "OPTION_2":
                sendMessage(chatId, String.format("✅ %s, you selected Option 2!", username));
                break;

            default:
                sendMessage(chatId, String.format("❌ %s, unknown selection: %s", username, callbackData));
                break;
        }

        // Responder a Telegram para quitar la animación de loading
        try {
            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(callbackQuery.getId());
            answer.setText("Received your selection!");
            execute(answer);  // ✅ aquí ya es correcto
        } catch (TelegramApiException e) {
            logger.error("Failed to answer callback query: {}", e.getMessage());
        }
    }




}
