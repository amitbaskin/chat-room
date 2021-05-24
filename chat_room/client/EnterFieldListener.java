package chat_room.client;

import chat_room.server.Server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class EnterFieldListener implements ActionListener {
    /*
    Sending the message entered in the enter field by the client, which is read by the singleClientHandler
    associated with the connection of this client.
     */
    public final static String ERROR_MSG = "\nCould not send your message\n";
    public final static String ERROR_TITLE = "ERROR";
    public final static String STR_PAD = "_";

    private final Client client;

    public EnterFieldListener(Client client){
        this.client = client;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String input = e.getActionCommand();
            // avoid closing connection because of actual input
            if (input.equals(Server.SERVER_TERMINATE) || input.equals(Client.TERMINATE)) input += STR_PAD;
            client.write(input);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(client, ERROR_MSG, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        } client.getEnterField().setText(Client.DEFAULT_TEXT);
    }
}