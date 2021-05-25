package chat_room.client;

import chat_room.server.Server;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 Sending the message entered in the enter field by the client, which is read by the singleClientHandler
 associated with the connection of this client.
 */
public class EnterFieldListener implements ActionListener {
    private final static String DEFAULT_TEXT = "";
    private final static String ERROR_MSG = "\nCould not send your message\n";
    private final static String ERROR_TITLE = "ERROR";
    private final static String STR_PAD = "_";
    private final Client client;

    /**
     * Create a new field listener
     * @param client The client associated with this text field listener
     */
    public EnterFieldListener(Client client){
        this.client = client;
    }

    /**
     * The action to perform when the user presses enters when in the text field
     * @param e The event that triggered this listener
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String input = e.getActionCommand();
            // avoid closing connection because of actual input
            if (input.equals(Server.SERVER_TERMINATE) || input.equals(Client.TERMINATE)) input += STR_PAD;
            client.write(input);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(client, ERROR_MSG, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        } client.getEnterField().setText(DEFAULT_TEXT);
    }
}