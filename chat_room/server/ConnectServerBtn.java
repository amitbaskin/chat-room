package chat_room.server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnectServerBtn extends JButton {
    private static final String CONNECT = "Connect";
    ConnectServerBackground serverConnectInBackground;

    public ConnectServerBtn(final Server server){
        super(CONNECT);
//        serverConnectInBackground = new ConnectServerBackground(server);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                serverConnectInBackground.doInBackground();
                server.execute();
            }
        });
    }
}