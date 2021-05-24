package chat_room.server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ParticipantsLimitFieldListener implements ActionListener {
    private static final String PARTICIPANTS_LIMIT_TITLE = "Participants Limit";
    private static final String ERR_TITLE = "ERROR";
    private static final String PARTICIPANTS_LIMIT_MSG = "The participants limit now is: %d";
    private static final String INPUT_ERR_MSG = "Illegal input entered.\nLimit unchanged.";
    private final Server server;

    public ParticipantsLimitFieldListener(Server server) {
        this.server = server;
    }

    private void limitMsg(int limit){
        JOptionPane.showMessageDialog(server, String.format(PARTICIPANTS_LIMIT_MSG, limit),
                PARTICIPANTS_LIMIT_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

    private void inputErrMsg(){
        JOptionPane.showMessageDialog(server, INPUT_ERR_MSG, ERR_TITLE, JOptionPane.ERROR_MESSAGE);
    }

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