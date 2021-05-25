package chat_room.server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class DisconnectServerBtn extends JButton {
    private static final String DISCONNECT = "Disconnect";
    private static final String DISCONNECTING_MSG = "Disconnecting... Please wait.";

    public DisconnectServerBtn(final Server server){
        super(DISCONNECT);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.getParticipantsArea().setText(DISCONNECTING_MSG);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new SwingWorker<Object, Object>() {
                            @Override
                            protected Object doInBackground() throws Exception {
                                server.disconnect();
                                return null;
                            }
                        }.execute();
                        server.getParticipantsArea().setText(Server.DISCONNECTED);
                    }
                });
            }
        });
    }
}