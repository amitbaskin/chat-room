package chat_room.client;

import javax.swing.*;

public class ClientBackground extends SwingWorker<Object, Object> {
    /*
    Created by pressing the "Join" button. Runs the process of the client behind the gui: connecting to the
     server, processing messages and eventually closing the connection if reads "TERMINATE" that was sent
     because the "Quit" button was pressed
     */
    private final Client client;
    public ClientBackground(Client client){
        this.client = client;
    }


    @Override
    protected Object doInBackground() {
        client.getExecutorService().execute(client);
        return null;
    }
}