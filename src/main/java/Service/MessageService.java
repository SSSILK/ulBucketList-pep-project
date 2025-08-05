package Service;

import DAO.MessageDao;
import Model.Account;
import Model.Message;
import java.util.List;

public class MessageService {
    private MessageDao messageDao;
    private AccountService accountService;

    public MessageService() {
        this.messageDao = new MessageDao();
        this.accountService = new AccountService();
    }

    public Message createMessage(Message message) {
        // Check if message is null or has invalid text
        if (message == null || message.getMessage_text() == null || message.getMessage_text().isBlank()) {
            return null;
        }
        if (message.getMessage_text().length() > 255) {
            return null;
        }
        // Check if user exists
        Account user = accountService.getAccountById(message.getPosted_by());
        if (user == null) {
            return null;
        }

        return messageDao.sendMessage(message);
    }

    public List<Message> getAllMessages() {
        return messageDao.getAllMessages();
    }

    public Message getMessageById(int message_id) {
        return messageDao.getMessageById(message_id);
    }

  public Message updateMessage(int messageID, String newText) {
    Message existing = messageDao.getMessageById(messageID);
    if (existing == null) {
        return null;
    }
    existing.setMessage_text(newText);
   messageDao.updateMessage(messageID, newText);
    return existing;
}


    public Message deleteMessage(int message_id) {
        // Return the deleted message from DAO (or null if not found)
        return messageDao.deleteMessage(message_id);
    }

    public List<Message> getMessagesByUserId(int accountId) {
    return messageDao.getMessagesByUserId(accountId);
}

}
