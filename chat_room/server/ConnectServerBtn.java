package chat_room.server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A button for a server to connect
 */
public class ConnectServerBtn extends JButton {
    private static final String CONNECT = "Connect";

    /**
     * Create a new button
     * @param server The server associated with this button
     */
    public ConnectServerBtn(final Server server){
        super(CONNECT);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!server.isConnected()){
                    server.run();
                }
            }
        });
    }
}