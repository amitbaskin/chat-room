package chat_room.server;

import chat_room.ChatParticipant;
import chat_room.Connection;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class AllClientsMaintainer {
    /*
    Used by the singleClientHandler objects in in order to keep track of the clients participating in the
    chat room
     */
    private final static String CHAT_PARTICIPANTS_TITLE = "Chat Participants: (%d)\n";
    public final static String DEFAULT_TEXT = "";
    private static final String JOINED_MSG = "joined";
    private static final String LEFT_MSG = "left";
    private static final String MSG_FORMAT = "\n%s %s";
    private final ConcurrentHashMap<String, Connection> connections;
    private final Server server;

    public synchronized ConcurrentHashMap<String, Connection> getConnections() {
        return connections;
    }

    public AllClientsMaintainer(Server server){
        connections = new ConcurrentHashMap<>();
        this.server = server;
    }

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

    private void sendAddMessages(String tempName, Connection connection) throws IOException {
        connection.getOutputObjectStream().writeObject(tempName);
        sendToAllButOne(getAddConnectionMsg(connection), connection.getMyName());
        sendToAllButOne(new ChatParticipant(connection.getMyName()), connection.getMyName());
    }

    private void handleAddedClient(String tempName, Connection connection) throws IOException {
        getConnections().put(connection.getMyName(), connection);
        sendAddMessages(tempName, connection);
        updateParticipantsArea();
        server.increaseParticipantAMount();
    }

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

    private void sendRemoveMessages(String connection) throws IOException {
        sendToAll(getRemoveConnectionMsg(connection));
        sendToAll(new ChatParticipant(connection));
    }

    public void removeClient(String connection) throws IOException {
        connections.remove(connection);
        sendRemoveMessages(connection);
        updateParticipantsArea();
        server.decreaseParticipantAmount();
    }

    public void notifyClientOfParticipants(Connection connection) throws IOException {
        for (String name : connections.keySet()) connection.send(new ChatParticipant(name));
    }


    public void sendToAllButOne(Object msg, String specificConnection) throws IOException {
        for (String curConnection : connections.keySet()){
            if (!curConnection.equals(specificConnection)) connections.get(curConnection).send(msg);
        }
    }

    public void sendToAll(Object msg) throws IOException {
        for (Connection curConnection : connections.values()){
            curConnection.send(msg);
        }
    }

    private String getAddConnectionMsg(Connection connection){
        return String.format(MSG_FORMAT, connection.getMyName(), JOINED_MSG);
    }

    private String getRemoveConnectionMsg(String connection){
        return String.format(MSG_FORMAT, connection, LEFT_MSG);
    }
}