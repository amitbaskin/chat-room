package chat_room.client;

import chat_room.Connection;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 Creates an instance of clientBackground which connects the client to the server and allows it to
 participate in the chat room
 */
public class  JoinBtnListener implements ActionListener {
    private final static String DEFAULT_TEXT = "";
    private static final String ERR_MSG = "You are already connected!";
    private static final String ERR_TITLE = "ERROR";
    private final Client client;

    /**
     * Create a new listener
     * @param client The client associated with this listener
     */
    public JoinBtnListener(Client client) {
        this.client = client;
    }

    /**
     * Display an error message
     */
    private void errMsg(){
        JOptionPane.showMessageDialog(client, ERR_MSG, ERR_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * The action to perform as a result of the user pressing the join button
     * @param e The event that triggered this listener
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Connection connection = client.getConnection();
        if (connection == null || client.getIsClosed()) {
            client.getDisplayArea().setText(DEFAULT_TEXT);
            client.run();
        } else errMsg();
    }
}