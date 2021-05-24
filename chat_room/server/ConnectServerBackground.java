package chat_room.server;

import javax.swing.*;

public class ConnectServerBackground extends SwingWorker<Object, Object> {
    private final Server server;

    public ConnectServerBackground(Server server){
        this.server = server;
    }

    @Override
    protected Object doInBackground() {
        if (!server.isConnected()){
            server.connectServerBackground();
        } return null;
    }
}