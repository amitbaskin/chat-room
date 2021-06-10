package chat_room.server;

import chat_room.Connection;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static java.lang.System.exit;

/**
 Accepts clients to the chat room and creates a singleClientHandler for each in order to read messages
 from each one and then broadcast to all clients in the chat room
 */
public class Server extends JFrame {
    private final static String ERR_TITLE = "ERROR";
    private final static String KILL_SERVER_MSG = "Critical error has occurred.\nClosing server.";
    private final static String SERVER_TITLE = "Server";
    private final static int WINDOW_ROWS = 500;
    private final static int WINDOW_COLS = 200;
    final static String CONNECTED = "Connected\n\n";
    final static String DISCONNECTED = "Disconnected\n\n";

    /**
     * A signal saying this server has terminated
     */
    public final static String SERVER_TERMINATE = "SERVER_TERMINATE";

    /**
     * A signal saying the chat room is full
     */
    public final static String FULL_SIGNAL = "FULL";

    /**
     * The default port of this server
     */
    public final static int DEFAULT_PORT = 12345;

    /**
     * The default queue length of this server
     */
    public final static int DEFAULT_QUEUE_LENGTH = 10;

    private ServerSocket serverSocket;
    private final ExecutorService executorService;
    private boolean isConnected;
    private final JTextArea participantsArea;
    private int currentParticipantAmount = 0;
    private AllClientsMaintainer allClientsMaintainer;
    private int maxParticipantsAmount;

    /**
     * Create a new server
     * @param executorService The service to handle the incoming connections
     */
    public Server(final ExecutorService executorService) {
        super(SERVER_TITLE);
        this.executorService = executorService;
        isConnected = false;
        add(new ConnectServerBtn(this), BorderLayout.EAST);
        add(new DisconnectServerBtn(this), BorderLayout.WEST);
        add(new ParticipantsLimitField(this), BorderLayout.SOUTH);
        participantsArea = new JTextArea();
        participantsArea.setText(DISCONNECTED);
        participantsArea.setEditable(false);
        add(new JScrollPane(participantsArea), BorderLayout.CENTER);
        setSize(WINDOW_ROWS, WINDOW_COLS);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Get the maximum amount of permitted participants
     * @return The maximum amount
     */
    public int getMaxParticipantsAmount() {
        return maxParticipantsAmount;
    }

    /**
     * Increases the amount of participants in the chat room
     */
    public void increaseParticipantAmount(){
        currentParticipantAmount += 1;
    }

    /**
     * Decreases the amount of participants in the chat room
     */
    public void decreaseParticipantAmount(){
        currentParticipantAmount -= 1;
    }

    /**
     * Sets the connection status of this server
     * @param isConnected The status to set
     */
    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    /**
     * Gets the text area where the participants are displayed
     * @return The text area
     */
    public JTextArea getParticipantsArea() {
        return participantsArea;
    }

    /**
     * Gets the socket of this server
     * @return The socket
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * Gets the maintainer holding the connections of the client sin the chat room
     * @return The maintainer
     */
    public AllClientsMaintainer getAllClientsMaintainer() {
        return allClientsMaintainer;
    }

    /**
     * Gets the current participants amount
     * @return The amount
     */
    public int getCurrentParticipantAmount() { return currentParticipantAmount; }

    /**
     * Gets whether or not this server is connected
     * @return This server's connection status
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Sets the current participants amount to zero
     */
    public void restartParticipantsAmount() {
        this.currentParticipantAmount = 0;
    }

    /**
     * Sets the maximum of participants allowed
     * @param maxParticipantsAmount The maximum to set
     */
    public void setMaxParticipantsAmount(int maxParticipantsAmount) {
        this.maxParticipantsAmount = maxParticipantsAmount;
    }

    /**
     * Initializes the clients' connections' maintainer
     */
    private void initializeAllClientsMaintainer(){
        if (allClientsMaintainer == null) allClientsMaintainer = new AllClientsMaintainer(this,
                 executorService);
        else allClientsMaintainer.getConnections().clear();
    }

    /**
     * Handles a new connection
     * @param connection The new connection
     * @throws IOException In case there's a problem notifying the new client of the exiting participants
     */
    private void handleNewConnection(Connection connection) throws IOException {
        allClientsMaintainer.addClient(connection);
        allClientsMaintainer.notifyClientOfParticipants(connection);
        executorService.execute(new SingleClientHandler(connection, allClientsMaintainer));
    }

    /**
     * Enrolls new clients to the chat room
     */
    public void processConnections() {
        initializeAllClientsMaintainer();
        try{
            serverSocket = new ServerSocket(DEFAULT_PORT, DEFAULT_QUEUE_LENGTH);
            Connection connection;
            while(isConnected){
                connection = new Connection(serverSocket.accept(), "");
                String firstInput = (String) connection.read();
                if (firstInput.equals(SERVER_TERMINATE)) {
                    close();
                    return;
                } if (currentParticipantAmount >= maxParticipantsAmount){
                    connection.send(FULL_SIGNAL);
                    continue;
                } connection.setMyName(firstInput);
                handleNewConnection(connection);
            }
        } catch (IOException | ClassNotFoundException ignore){}
    }

    /**
     * Helps closing the server
     * @throws IOException In case there's a problem notifying the exiting participants of the server
     * termination
     */
    private void closeHelper() throws IOException {
        setIsConnected(false);
        for (Connection singleClientConnection :
                getAllClientsMaintainer().getConnections().values()){
            singleClientConnection.send(Server.SERVER_TERMINATE);
        } getServerSocket().close();
        restartParticipantsAmount();
    }

    /**
     * Kills the server
     */
    public void killServer(){
        JOptionPane.showMessageDialog(this, KILL_SERVER_MSG, ERR_TITLE, JOptionPane.ERROR_MESSAGE);
        exit(1);
    }

    /**
     * Closes the server
     */
    public void close(){
        try {
            closeHelper();
        } catch (IOException exception) {
            killServer();
        }
    }

    /**
     * Disconnects the server in the background
     */
    public void disconnect() {
        if (isConnected()) {
            new SwingWorker<Object, Object>(){
                @Override
                protected Object doInBackground() throws Exception {
                    Socket socket = new Socket(getServerSocket().getInetAddress(), Server.DEFAULT_PORT);
                    Connection connection = new Connection(socket, "");
                    connection.send(Server.SERVER_TERMINATE);
                    setIsConnected(false);
                    connection.close();
                    return null;
                }
            }.execute();

        }
    }

    /**
     * Runs the server
     */
    public void run(){
        setIsConnected(true);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getParticipantsArea().setText(Server.CONNECTED);
            }
        });
        new SwingWorker<Object, Object>(){
            @Override
            protected Object doInBackground() throws Exception {
                processConnections();
                return null;
            }
        }.execute();

    }
}