package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Model.Message;
import Util.ConnectionUtil;

public class MessageDao {

    public Message sendMessage(Message message) {
        String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        try (
            Connection connection = ConnectionUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setInt(1, message.getPosted_by());
            statement.setString(2, message.getMessage_text());
            statement.setLong(3, message.getTime_posted_epoch());
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        message.setMessage_id(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return message;
    }

    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message";

        try (
            Connection connection = ConnectionUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery()
        ) {
            while (rs.next()) {
                Message message = new Message();
                message.setMessage_id(rs.getInt("message_id"));
                message.setMessage_text(rs.getString("message_text"));
                message.setPosted_by(rs.getInt("posted_by"));
                message.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    public Message getMessageById(int message_id) {
        String sql = "SELECT * FROM message WHERE message_id = ?";
        try (
            Connection connection = ConnectionUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, message_id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Message message = new Message();
                    message.setMessage_id(rs.getInt("message_id"));
                    message.setMessage_text(rs.getString("message_text"));
                    message.setPosted_by(rs.getInt("posted_by"));
                    message.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
                    return message;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message deleteMessage(int message_id) {
        Message message = getMessageById(message_id);
        if (message == null) return null;

        String sql = "DELETE FROM message WHERE message_id = ?";
        try (
            Connection connection = ConnectionUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, message_id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return message;
    }

    public Message updateMessage(int message_id, String message_text) {
        Message existingMessage = getMessageById(message_id);
        if (existingMessage == null) return null;

        String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
        try (
            Connection connection = ConnectionUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, message_text);
            statement.setInt(2, message_id);
            statement.executeUpdate();
            return getMessageById(message_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Message> getMessagesByUserId(int accountId) {
    List<Message> messages = new ArrayList<>();

    try (Connection conn = ConnectionUtil.getConnection()) {
        String sql = "SELECT * FROM message WHERE posted_by = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, accountId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Message message = new Message(
                rs.getInt("message_id"),
                rs.getInt("posted_by"),
                rs.getString("message_text"),
                rs.getLong("time_posted_epoch")
            );
            messages.add(message);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return messages;
}

}
