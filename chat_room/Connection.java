package chat_room;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {
    /*
    Constitutes a connection (usually between a client and the server. However an end point can send
    messages to itself using a connection to itself). Both the server and each client hold their own
    instance of the connection.
     */
    private final Socket socket;
    private final ObjectOutputStream outputObjectStream;
    private final ObjectInputStream inputObjectStream;
    private String myName;

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName){
        this.myName = myName;
    }

    public Connection(Socket socket, String myName) throws IOException {
        this.myName = myName;
        this.socket = socket;
        outputObjectStream = new ObjectOutputStream(socket.getOutputStream());
        outputObjectStream.flush();
        inputObjectStream = new ObjectInputStream(socket.getInputStream());
    }

    public Socket getSocket() {
        return socket;
    }
    public ObjectInputStream getInputObjectStream() {
        return inputObjectStream;
    }
    public ObjectOutputStream getOutputObjectStream() { return outputObjectStream; }

    public Object read() throws IOException, ClassNotFoundException {
        return inputObjectStream.readObject();
    }

    public void close() throws IOException {
        socket.close();
        outputObjectStream.close();
        inputObjectStream.close();
    }

    public synchronized void send(Object msg) throws IOException {
        outputObjectStream.writeObject(msg);
        outputObjectStream.flush();
    }
}