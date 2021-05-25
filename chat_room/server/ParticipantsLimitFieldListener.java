package chat_room.server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A listener for a participants-limit-field
 */
public class ParticipantsLimitFieldListener implements ActionListener {
    private static final String PARTICIPANTS_LIMIT_TITLE = "Participants Limit";
    private static final String ERR_TITLE = "ERROR";
    private static final String PARTICIPANTS_LIMIT_MSG = "The participants limit now is: %d";
    private static final String INPUT_ERR_MSG = "Illegal input entered.\nLimit unchanged.";
    private final Server server;

    /**
     * Create a new listener
     * @param server The server associated with this listener
     */
    public ParticipantsLimitFieldListener(Server server) {
        this.server = server;
    }

    /**
     * A message to display regarding the limit
     * @param limit The limit set
     */
    private void limitMsg(int limit){
        JOptionPane.showMessageDialog(server, String.format(PARTICIPANTS_LIMIT_MSG, limit),
                PARTICIPANTS_LIMIT_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * A message to display regarding an invalid limit
     */
    private void inputErrMsg(){
        JOptionPane.showMessageDialog(server, INPUT_ERR_MSG, ERR_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * The action to perform when the server operator presses enters in the participants-limit-field
     * @param e The event triggering this listener
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String entered = e.getActionCommand();
        try{
            int limit = Integer.parseInt(entered);
            server.setMaxParticipantsAmount(limit);
            limitMsg(limit);
        } catch (NumberFormatException numberFormatException) {
            inputErrMsg();
        }
    }
}