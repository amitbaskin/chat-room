package chat_room.server;

import chat_room.client.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class QuitBtnListener implements ActionListener {
    /*
    Invokes the client's closingHelper() method which prepares the closing of the connection to the server
     */
    private static final String ERR_MSG = "You are not connected!";
    private static final String ERR_TITLE = "ERROR";
    private final Client client;

    public QuitBtnListener(Client client){
        this.client = client;
    }

    private void errMsg(){
        JOptionPane.showMessageDialog(client, ERR_MSG, ERR_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean isPreClosingSuccessful = client.closingHelper();
        if (!isPreClosingSuccessful) errMsg();
    }
}