package chat_room.server;

import javax.swing.*;
import java.io.IOException;

public class DisconnectServerBackground extends SwingWorker<Object, Object> {
    private final Server server;

    public DisconnectServerBackground(Server server){
        this.server = server;
    }

    @Override
    protected Object
    doInBackground() throws IOException {
        server.disconnect();
        return null;
    }
}