package chat_room;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 Constitutes a connection (usually between a client and the server. However an end point can send
 messages to itself using a connection to itself). Both the server and each client hold their own
 instance of the connection.
 */
public class Connection {
    private final Socket socket;
    private final ObjectOutputStream outputObjectStream;
    private final ObjectInputStream inputObjectStream;
    private String myName;

    /**
     * Create a new connection
     * @param socket The socket of this connection
     * @param myName The name of this connection
     * @throws IOException In case There's a problem creating new streams
     */
    public Connection(Socket socket, String myName) throws IOException {
        this.myName = myName;
        this.socket = socket;
        outputObjectStream = new ObjectOutputStream(socket.getOutputStream());
        outputObjectStream.flush();
        inputObjectStream = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Gets this connection's name
     * @return The name
     */
    public String getMyName() {
        return myName;
    }

    /**
     * Sets this connection's name
     * @param myName The nam eto set
     */
    public void setMyName(String myName){
        this.myName = myName;
    }

    /**
     * Gets the socket of this connection
     * @return The socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Gets the input stream of this connection
     * @return The stream
     */
    public ObjectInputStream getInputObjectStream() {
        return inputObjectStream;
    }

    /**
     * Gets the output stream of this connection
     * @return The stream
     */
    public ObjectOutputStream getOutputObjectStream() {
        return outputObjectStream;
    }

    /**
     * Reads an object from the input stream
     * @return The object read
     * @throws IOException In case there's a problem reading
     * @throws ClassNotFoundException In case the class of the object read is unrecognized
     */
    public Object read() throws IOException, ClassNotFoundException {
        return inputObjectStream.readObject();
    }

    /**
     * Closes this connection
     * @throws IOException In case there's a problem closing
     */
    public void close() throws IOException {
        socket.close();
        outputObjectStream.close();
        inputObjectStream.close();
    }

    /**
     * Sends a message
     * @param msg The message to send
     * @throws IOException In case there's a problem sending
     */
    public synchronized void send(Object msg) throws IOException {
        outputObjectStream.writeObject(msg);
        outputObjectStream.flush();
    }
}