package chat_room.server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnectServerBtn extends JButton {
    private static final String CONNECT = "Connect";

    public ConnectServerBtn(final Server server){
        super(CONNECT);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<Object, Object>() {
                    @Override
                    protected Object doInBackground() {
                        if (!server.isConnected()){
                            server.run();
                        } return null;
                    }
                }.execute();
            }
        });
    }
}