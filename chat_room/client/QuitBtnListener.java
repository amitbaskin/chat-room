package chat_room.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 Invokes the client's closingHelper() method which prepares the closing of the connection to the server
 */
public class QuitBtnListener implements ActionListener {
    private static final String ERR_MSG = "You are not connected!";
    private static final String ERR_TITLE = "ERROR";
    private final Client client;

    /**
     * Create a new quit-button listener
     * @param client The client associated with this button
     */
    public QuitBtnListener(Client client){
        this.client = client;
    }

    /**
    Displaying an error message regarding quiting the chat room
     */
    private void errMsg(){
        JOptionPane.showMessageDialog(client, ERR_MSG, ERR_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Yhe action to perform when the user presses this quit button
     * @param e The event that triggered this listener
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        boolean isPreClosingSuccessful = client.closingHelper();
        if (!isPreClosingSuccessful) errMsg();
    }
}