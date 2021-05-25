package chat_room.client;

import chat_room.Connection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class  JoinBtnListener implements ActionListener {
    /*
    Creates an instance of clientBackground which connects the client to the server and allows it to
    participate in the chat room
     */
    private static final String ERR_MSG = "You are already connected!";
    private static final String ERR_TITLE = "ERROR";
    private final Client client;

    public JoinBtnListener(Client client) {
        this.client = client;
    }

    private void errMsg(){
        JOptionPane.showMessageDialog(client, ERR_MSG, ERR_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Connection connection = client.getConnection();
        if (connection == null || client.getIsClosed()) {
            client.getDisplayArea().setText(Client.DEFAULT_TEXT);
            client.run();
        } else errMsg();
    }
}