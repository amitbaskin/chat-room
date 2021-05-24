package chat_room.server;

import chat_room.ChatParticipant;
import chat_room.Connection;
import chat_room.client.Client;

import java.io.IOException;

public class SingleClientHandler implements Runnable {
    /*
    Created by the server. Handles a connection with a client, continuously checking for messages from it
    an broadcasting them to the other clients when one is read.
     */
    private final static String MSG_TO_MYSELF_FORMAT = "\nYou (%s): %s";
    private final static String MSG_TO_ALL_FORMAT = "\n%s: %s";
    private final Connection connection;
    private final String connectionName;
    private final AllClientsMaintainer allClientsMaintainer;

    public SingleClientHandler(Connection connection,
                               AllClientsMaintainer allClientsMaintainer) {
        this.connection = connection;
        connectionName = connection.getMyName();
        this.allClientsMaintainer = allClientsMaintainer;
    }

    private void handleChatParticipantInput(Object input) throws IOException {
        String name;
        name = ((ChatParticipant) input).getName();
        for (String curName : allClientsMaintainer.getConnections().keySet()) {
            if (curName.equals(name)) {
                allClientsMaintainer.removeClient(name);
            }
        }
    }

    private void sendMessages(Object input) throws IOException {
        String msg;
        msg = String.format(MSG_TO_ALL_FORMAT, connection.getMyName(), input);
        allClientsMaintainer.sendToAllButOne(msg, connection.getMyName());
        msg = String.format(MSG_TO_MYSELF_FORMAT, connection.getMyName(), input);
        connection.send(msg);
    }

    @Override
    public void run() {
        try {
            Object input;
            while (true) {
                input = connection.read();
                if (input instanceof ChatParticipant) { // A signal to add or remove a participant
                    handleChatParticipantInput(input);
                    continue;
                } if (input.equals(Client.TERMINATE) || input.equals(Server.SERVER_TERMINATE)) {
                    connection.send(Client.TERMINATE);
                    return;
                } sendMessages(input);
            }
        } catch (IOException | ClassNotFoundException e) {
            try {
                connection.send(Client.TERMINATE);
            } catch (IOException exception) {
                try {
                    allClientsMaintainer.sendToAllButOne(new ChatParticipant(connectionName), connectionName);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}