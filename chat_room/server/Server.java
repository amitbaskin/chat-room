package chat_room.server;

import chat_room.Connection;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static java.lang.System.exit;

public class Server extends JFrame implements Runnable{
    /*
    Accepts clients to the chat room and creates a singleClientHandler for each in order to read messages
    from each one and then broadcast to all clients in the chat room
     */
    public final static String SERVER_TERMINATE = "SERVER_TERMINATE";
    public final static String ERR_TITLE = "ERROR";
    public final static String KILL_SERVER_MSG = "Critical error has occurred.\nClosing server.";
    public final static String CONNECTED = "Connected\n\n";
    public final static String DISCONNECTED = "Disconnected\n\n";
    public final static String FULL_SIGNAL = "FULL";
    public final static String SERVER_TITLE = "Server";
    public final static int DEFAULT_PORT = 12345;
    public final static int DEFAULT_QUEUE_LENGTH = 10;
    private final static int WINDOW_ROWS = 500;
    private final static int WINDOW_COLS = 200;

    private ServerSocket serverSocket;
    private final ExecutorService executorService;
    private boolean isConnected;
    private final JTextArea participantsArea;
    private int currentParticipantAmount = 0;
    private AllClientsMaintainer allClientsMaintainer;
    private int maxParticipantsAmount;

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

    public int getMaxParticipantsAmount() { return maxParticipantsAmount; }
    public void increaseParticipantAMount(){
        currentParticipantAmount += 1;
    }
    public void decreaseParticipantAmount(){
        currentParticipantAmount -= 1;
    }
    public void setIsConnected(boolean connected) {
        isConnected = connected;
    }
    public JTextArea getParticipantsArea() {
        return participantsArea;
    }
    public ServerSocket getServerSocket() {
        return serverSocket;
    }
    public AllClientsMaintainer getAllClientsMaintainer() {
        return allClientsMaintainer;
    }
    public int getCurrentParticipantAmount() { return currentParticipantAmount; }
    public boolean isConnected() {
        return isConnected;
    }

    public void setCurrentParticipantAmount(int currentParticipantAmount) {
        this.currentParticipantAmount = currentParticipantAmount;
    }

    public void setMaxParticipantsAmount(int maxParticipantsAmount) {
        this.maxParticipantsAmount = maxParticipantsAmount;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    private void initializeAllClientsMaintainer(){
        if (allClientsMaintainer == null) allClientsMaintainer = new AllClientsMaintainer(this);
        else allClientsMaintainer.getConnections().clear();
    }

    private void handleNewConnection(Connection connection) throws IOException {
        allClientsMaintainer.addClient(connection);
        allClientsMaintainer.notifyClientOfParticipants(connection);
        executorService.execute(new SingleClientHandler(connection, allClientsMaintainer));
    }

    public void execute(){
        connectServerBackground();
    }

    @Override
    public void run() {
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

    private void closeHelper() throws IOException {
        setIsConnected(false);
        for (Connection singleClientConnection :
                getAllClientsMaintainer().getConnections().values()){
            singleClientConnection.send(Server.SERVER_TERMINATE);
        } getServerSocket().close();
        setCurrentParticipantAmount(0);
    }

    public void killServer(){
        JOptionPane.showMessageDialog(this, KILL_SERVER_MSG, ERR_TITLE, JOptionPane.ERROR_MESSAGE);
        exit(1);
    }

    public void close(){
        try {
            closeHelper();
        } catch (IOException exception) {
            killServer();
        }
    }

    public void disconnectInBackground() throws IOException {
        Socket socket = new Socket(getServerSocket().getInetAddress(), Server.DEFAULT_PORT);
        Connection connection = new Connection(socket, "");
        connection.send(Server.SERVER_TERMINATE);
        setIsConnected(false);
        connection.close();
    }

    public void connectServerBackground(){
        setIsConnected(true);
        getParticipantsArea().setText(Server.CONNECTED);
        executorService.execute(this);
    }
}