package chat_room.server;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A button for disconnecting a server
 */
public class DisconnectServerBtn extends JButton {
    private static final String DISCONNECT = "Disconnect";
    private static final String DISCONNECTING_MSG = "Disconnecting... Please wait.";

    /**
     * Create a new button
     * @param server The server associated with this button
     */
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