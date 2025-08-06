package Controller;

import javax.security.auth.login.AccountNotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.IOException;
import java.util.List;

public class SocialMediaController {

    public Javalin startAPI() {
        Javalin app = Javalin.create();

        app.post("/register", this::registerHandler);
        app.post("/login", this::loginHandler);

        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getMessagesHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByUserHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);

        return app;
    }

    private void registerHandler(Context ctx) {
    Account account = ctx.bodyAsClass(Account.class);

    // Validate input strictly
    if (account == null 
        || account.getUsername() == null || account.getUsername().isBlank()
        || account.getPassword() == null || account.getPassword().length() < 4) {
        ctx.status(400).result("");  // <-- Return EMPTY string
        return;
    }

    AccountService accountService = new AccountService();
    Account registeredAccount = accountService.register(account);

    if (registeredAccount == null) {
        ctx.status(400).result("");  // <-- Return EMPTY string for duplicate
    } else {
        ctx.status(200).json(registeredAccount);
    }
}


    private void loginHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account loginRequest = mapper.readValue(ctx.body(), Account.class);

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (username == null || password == null) {
            ctx.status(401).result("");
            return;
        }

        AccountService accountService = new AccountService();

        try {
            Account loggedIn = accountService.logAccountService(username, password);
            ctx.status(200).json(loggedIn);
        } catch (AccountNotFoundException | Util.AccountNotFoundException e) {
            System.out.println(e.getMessage());
            ctx.status(401).result("");
        }
    }

   private void createMessageHandler(Context ctx) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    Message message = mapper.readValue(ctx.body(), Message.class);

    // Validate message_text
    if (message.getMessage_text() == null 
        || message.getMessage_text().isBlank() 
        || message.getMessage_text().length() > 255) {
        ctx.status(400).result("");
        return;
    }

    // Validate posted_by exists
    AccountService accountService = new AccountService();
    if (accountService.getAccountById(message.getPosted_by()) == null) {
        ctx.status(400).result("");
        return;
    }

    // Create the message
    MessageService messageService = new MessageService();
    Message created = messageService.createMessage(message);

    if (created == null) {
        // If creation fails for some reason
        ctx.status(400).result("");
        return;
    }

    // Return created message with 200 status (per test expectation)
    ctx.status(200).json(created);
}


    private void getMessagesHandler(Context ctx) {
        MessageService messageService = new MessageService();
        List<Message> messages = messageService.getAllMessages();
        ctx.status(200).json(messages);
    }

    private void getMessageByIdHandler(Context ctx) {
        try {
            int messageID = Integer.parseInt(ctx.pathParam("message_id"));
            MessageService messageService = new MessageService();
            Message message = messageService.getMessageById(messageID);

            if (message == null) {
                ctx.status(200).result("");
                return;
            }
            ctx.status(200).json(message);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid message ID");
        }
    }

    private void updateMessageHandler(Context ctx) throws JsonProcessingException {
        try {
            int messageID = Integer.parseInt(ctx.pathParam("message_id"));
            ObjectMapper mapper = new ObjectMapper();
            Message updatedData = mapper.readValue(ctx.body(), Message.class);

            String newText = updatedData.getMessage_text();
            if (newText == null || newText.isBlank() || newText.length() > 255) {
                ctx.status(400).result("");
                return;
            }

            MessageService messageService = new MessageService();
            Message updatedMessage = messageService.updateMessage(messageID, newText);

            if (updatedMessage == null) {
                ctx.status(400).result("");
                return;
            }
            ctx.status(200).json(updatedMessage);

        } catch (NumberFormatException e) {
            ctx.status(400).result("");
        }
    }

    private void deleteMessageHandler(Context ctx) {
        try {
            int messageID = Integer.parseInt(ctx.pathParam("message_id"));
            MessageService messageService = new MessageService();
            Message deleted = messageService.deleteMessage(messageID);

            if (deleted == null) {
                ctx.status(200).result("");
                return;
            }

            ctx.status(200).json(deleted);
            System.out.println("Message deleted");
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid message ID");
        }
    }

    private void getMessagesByUserHandler(Context ctx) {
    try {
        int accountId = Integer.parseInt(ctx.pathParam("account_id"));

        MessageService messageService = new MessageService();
        List<Message> messages = messageService.getMessagesByUserId(accountId);

        ctx.status(200).json(messages);  // Empty list is fine if user has no messages
    } catch (NumberFormatException e) {
        ctx.status(400).result("Invalid account ID");
    }
}

}
