package chat_room.server;

import chat_room.ChatParticipant;
import chat_room.Connection;
import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 Used by the singleClientHandler objects in in order to keep track of the clients participating in the
 chat room
 */
public class AllClientsMaintainer {
    private final static String CHAT_PARTICIPANTS_TITLE = "Chat Participants: (%d)\n";
    private final static String DEFAULT_TEXT = "";
    private static final String JOINED_MSG = "joined";
    private static final String LEFT_MSG = "left";
    private static final String MSG_FORMAT = "\n%s %s";
    private final ConcurrentHashMap<String, Connection> connections;
    private final Server server;

    /**
     * Create a new maintainer
     * @param server The server associated with this maintainer
     */
    public AllClientsMaintainer(Server server){
        connections = new ConcurrentHashMap<>();
        this.server = server;
    }

    /**
     * Get the connections hat this maintainer holds
     * @return The connections
     */
    public synchronized ConcurrentHashMap<String, Connection> getConnections() {
        return connections;
    }

    /**
     * Update the participants area of the server
     */
    private void updateParticipantsArea(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JTextArea participantsArea = server.getParticipantsArea();
                participantsArea.setText(DEFAULT_TEXT);
                participantsArea.append(Server.CONNECTED);
                participantsArea.append(String.format(CHAT_PARTICIPANTS_TITLE,
                         server.getCurrentParticipantAmount()));
                for (String client : connections.keySet()){
                    participantsArea.append(client + "\n");
                }
            }
        });
    }

    /**
     * Send messages notifying of a new user joined
     * @param tempName The temporary name of this user
     * @param connection The connection with this user
     * @throws IOException In case there's a problem sending a message
     */
    private void sendAddMessages(String tempName, Connection connection) throws IOException {
        connection.getOutputObjectStream().writeObject(tempName);
        sendToAllButOne(getAddConnectionMsg(connection), connection.getMyName());
        sendToAllButOne(new ChatParticipant(connection.getMyName()), connection.getMyName());
    }

    /**
     * Handles the scenario of a new client joining
     * @param tempName The temporary name of the joining client
     * @param connection The connection with the joining client
     * @throws IOException In case there's a problem sending a message
     */
    private void handleAddedClient(String tempName, Connection connection) throws IOException {
        getConnections().put(connection.getMyName(), connection);
        sendAddMessages(tempName, connection);
        updateParticipantsArea();
        server.increaseParticipantAmount();
    }

    /**
     * Adds a new client
     * @param connection The connection with the client to add
     * @throws IOException In case there's a problem sending a message
     */
    public void addClient(Connection connection) throws IOException {
        int padding = -1;
        String tempName;
        String name = connection.getMyName();
        tempName = name;
        while (getConnections().containsKey(tempName)){
            tempName = name;
            padding += 1;
            tempName += padding;
        } connection.setMyName(tempName);
        handleAddedClient(tempName, connection);
    }

    /**
     * Notifying the clients in the chat room the a user has left
     * @param connection The connection of the leaving user
     * @throws IOException In case there's a problem sending a message
     */
    private void sendRemoveMessages(String connection) throws IOException {
        sendToAll(getRemoveConnectionMsg(connection));
        sendToAll(new ChatParticipant(connection));
    }

    /**
     * Removes a client
     * @param connection The connection with the client to remove
     * @throws IOException In case there's a problem sending a message
     */
    public void removeClient(String connection) throws IOException {
        connections.remove(connection);
        sendRemoveMessages(connection);
        updateParticipantsArea();
        server.decreaseParticipantAmount();
    }

    /**
     * Notifies a client of the participants in the chat room
     * @param connection The connection with client to notify
     * @throws IOException In case there's a problem sending a message
     */
    public void notifyClientOfParticipants(Connection connection) throws IOException {
        for (String name : connections.keySet()) connection.send(new ChatParticipant(name));
    }

    /**
     * Sending a message to everyone in the chat room except one
     * @param msg The message to send
     * @param specificConnection The connection to exclude
     * @throws IOException In case there's a problem sending a message
     */
    public void sendToAllButOne(Object msg, String specificConnection) throws IOException {
        for (String curConnection : connections.keySet()){
            if (!curConnection.equals(specificConnection)) connections.get(curConnection).send(msg);
        }
    }

    /**
     * Sends a message to all participants in the chat room
     * @param msg The message to send
     * @throws IOException In case there's a problem sending a message
     */
    public void sendToAll(Object msg) throws IOException {
        for (Connection curConnection : connections.values()){
            curConnection.send(msg);
        }
    }

    /**
     * Gets the message to send regarding the joining of specific client
     * @param connection The connection with joining client
     * @return The message to send
     */
    private String getAddConnectionMsg(Connection connection){
        return String.format(MSG_FORMAT, connection.getMyName(), JOINED_MSG);
    }

    /**
     * Gets the message to send regarding the leaving of specific client
     * @param connection The connection with leaving client
     * @return The message to send
     */
    private String getRemoveConnectionMsg(String connection){
        return String.format(MSG_FORMAT, connection, LEFT_MSG);
    }
}