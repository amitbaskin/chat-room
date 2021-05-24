package chat_room.server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class DisconnectServerBtn extends JButton {
    private static final String DISCONNECT = "Disconnect";
    private static final String DISCONNECTING_MSG = "Disconnecting... Please wait.";
    private final DisconnectServerBackground serverDisconnectInBackground;

    public DisconnectServerBtn(final Server server){
        super(DISCONNECT);
        serverDisconnectInBackground = new DisconnectServerBackground(server);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.getParticipantsArea().setText(DISCONNECTING_MSG);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            serverDisconnectInBackground.doInBackground();
                        } catch (IOException exception) {
                            server.killServer();
                        } server.getParticipantsArea().setText(Server.DISCONNECTED);
                    }
                });
            }
        });
    }
}