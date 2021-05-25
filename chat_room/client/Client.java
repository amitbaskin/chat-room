package chat_room.client;

import chat_room.ChatParticipant;
import chat_room.server.QuitBtnListener;
import chat_room.server.Server;
import chat_room.Connection;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
public class Client extends JFrame {
    /*
    The client's end in a connection with the server.
     */
    private final static String ERR_TITLE = "ERROR";
    private final static String CHAT_NAME_TITLE = "Chat Name";
    private final static String DEFAULT_HOST = "127.0.0.1";
    private final static String DEFAULT_NAME = "Client";
    private final static String FULL_ROOM_MSG = "The chat room is currently full. Please try again later.";
    private final static String HOST_NAME_REQUEST_MSG = "Please enter a host name:";
    private final static String CONNECTION_ERR_MSG = "Couldn't connect";
    private final static String SERVER_CLOSED_MSG = "The server has closed the connection";
    private final static String UNEXPECTED_ERR_MSG = "An unexpected problem has occurred";
    private final static String READ_ERR_MSG = "Experiencing problems reading incoming messages";
    private final static String CONNECTION_ATTEMPT_MSG = "Attempting connection...\n";
    private final static String CONNECTION_SUCCESS_MSG = "Connected to: ";
    private final static String CHAT_NAME_MSG_FORMAT = "Your name in this chat is: %s";
    private final static String CLOSING_CONNECTION_MSG = "\nClosing connection\n";
    private final static String CLOSED_MSG = "\nConnection is closed\n";
    private final static String INPUT_REQUEST = "Please input your name:";
    private final static String DEFAULT_FRAME_TITLE = "Client";
    private final static String CHAT_PARTICIPANTS_TITLE = "Chat Participants: (%d)\n";
    public final static String DEFAULT_TEXT = "";
    private final static String QUIT = "Quit";
    private final static String JOIN = "Join";
    public final static String TERMINATE = "TERMINATE";
    private final static String CONNECTION_SUCCESS_FORMAT = "\n%s%s\n";
    private final static String GET_MSG_FORMAT = "\n%s\n";
    private final static int WINDOW_ROWS = 500;
    private final static int WINDOW_COLS = 500;

    private String myName;
    private final JTextField enterField;
    private final JTextArea displayArea;
    private final JTextArea participantsArea;
    private Connection connection;
    private final ConcurrentHashMap<String, ChatParticipant> participants;
    private boolean isClosed;

    public Client() {
        super(DEFAULT_FRAME_TITLE);
        participants = new ConcurrentHashMap<>();
        enterField = new JTextField();
        enterField.setEditable(false);
        enterField.addActionListener(new EnterFieldListener(this));
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        participantsArea = new JTextArea();
        participantsArea.setEditable(false);
        participantsArea.setColumns(15);
        participantsArea.setRows(10);
        isClosed = true;
        add(new JScrollPane(displayArea), BorderLayout.CENTER);
        add(enterField, BorderLayout.SOUTH);
        JButton quitBtn = new JButton(QUIT);
        JButton joinBtn = new JButton(JOIN);
        quitBtn.addActionListener(new QuitBtnListener(this));
        joinBtn.addActionListener(new JoinBtnListener(this));
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridBagLayout());
        btnPanel.add(quitBtn);
        btnPanel.add(joinBtn);
        add(btnPanel, BorderLayout.NORTH);
        add(new JScrollPane(participantsArea), BorderLayout.EAST);
        setSize(WINDOW_ROWS, WINDOW_COLS);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public String getMyName() { return myName; }
    public ConcurrentHashMap<String, ChatParticipant> getParticipants() { return participants; }
    public Connection getConnection() { return connection; }
    public void setIsClosed(boolean closed) { isClosed = closed; }
    public boolean getIsClosed() { return isClosed; }
    public JTextField getEnterField() { return enterField; }
    public JTextArea getDisplayArea() { return displayArea; }
    public void write(String msg) throws IOException { connection.getOutputObjectStream().writeObject(msg); }

    public void run(){
        new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() {
                try {
                    connectToServer();
                    if (myName != null) {
                        processConnection();
                        closeConnection();
                    }
                } catch (ConnectionException | IOException e) {
                    displayArea.setText(DEFAULT_TEXT);
                }
                return null;
            }
        }.execute();
    }

    private InetAddress getInputHost() throws UnknownHostException {
        String hostName = JOptionPane.showInputDialog(this, HOST_NAME_REQUEST_MSG, DEFAULT_HOST);
        return InetAddress.getByName(hostName);
    }

    private Socket getNewSocket(InetAddress host) throws IOException {
        displayMsg(CONNECTION_ATTEMPT_MSG);
        return new Socket(host, Server.DEFAULT_PORT);
    }

    private String getFirstInput(Socket socket) throws IOException, ClassNotFoundException {
        connection = new Connection(socket, myName);
        connection.send(myName); // confirm name chosen with server
        return (String) connection.read();
    }

    private void getNameInput() {
        myName = JOptionPane.showInputDialog(this, INPUT_REQUEST, DEFAULT_NAME);
    }

    private void showFullRoomMsg(){
        JOptionPane.showMessageDialog(this, FULL_ROOM_MSG, ERR_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    private void informOfChatNameSelected(){
        JOptionPane.showMessageDialog(this, String.format(CHAT_NAME_MSG_FORMAT, myName), CHAT_NAME_TITLE,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void connectionSuccessScenario(InetAddress host){
        informOfChatNameSelected();
        this.setTitle(myName);
        setIsClosed(false);
        displayMsg(String.format(CONNECTION_SUCCESS_FORMAT, CONNECTION_SUCCESS_MSG, host));
    }

    private void connectionErrMsg(){
        JOptionPane.showMessageDialog(this, CONNECTION_ERR_MSG, ERR_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    private void unexpectedErrMsg(){
        JOptionPane.showMessageDialog(this, UNEXPECTED_ERR_MSG, ERR_TITLE,
                JOptionPane.ERROR_MESSAGE);
    }

    private void connectToServer() throws ConnectionException {
        try{
            InetAddress host = getInputHost();
            Socket socket = getNewSocket(host);
            getNameInput();
            if (myName == null) return; // if user pressed Esc or cancel
            String inputFromServer = getFirstInput(socket);
            if (inputFromServer.equals(Server.FULL_SIGNAL)){
                showFullRoomMsg();
                throw new ConnectionException();
            } myName = inputFromServer; // get this client's name set by the server (this name can be
            // different than the name this client chose if the he chose an empty string or a name that already
            // exists, in the this case his name will be padded by a digit
            connectionSuccessScenario(host);
        } catch (IOException exception) {
            connectionErrMsg();
            throw new ConnectionException();
        } catch (ClassNotFoundException e) {
            unexpectedErrMsg();
            throw new ConnectionException();
        }
    }

    private void serverClosedScenario(){
        JOptionPane.showMessageDialog(this, SERVER_CLOSED_MSG, ERR_TITLE,
                JOptionPane.ERROR_MESSAGE);
        getParticipants().clear();
    }

    private void chatParticipantInputScenario(Object input){
        ChatParticipant curChatParticipant = null;
        boolean isFoundParticipant = false;
        for (ChatParticipant chatParticipant : participants.values()) {
            if (chatParticipant.getName().equals(((ChatParticipant) input).getName())) {
                curChatParticipant = chatParticipant;
                isFoundParticipant = true;
                break;
            }
        } if (isFoundParticipant) {
            participants.remove(curChatParticipant.getName());
        } else {
            ChatParticipant participant = (ChatParticipant) input;
            participants.put(participant.getName(), participant);
        }
        updateParticipantsArea();
    }

    private void readErrMsg(){
        JOptionPane.showMessageDialog(this, READ_ERR_MSG, ERR_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    private void processConnection() {
        setTextFieldEditable(true);
        updateParticipantsArea();
        while (!getIsClosed()) {
            try {
                Object input;
                input = connection.read();
                if (input.equals(TERMINATE)) return;
                if (input.equals(Server.SERVER_TERMINATE)) {
                    serverClosedScenario();
                    return;
                } if (input instanceof ChatParticipant) {
                    chatParticipantInputScenario(input);
                } else displayMsg(String.format(GET_MSG_FORMAT, input));
            } catch (IOException | ClassNotFoundException exception) {
                readErrMsg();
                if (!isClosed) closingHelper();
            }
        }
    }

    public boolean closingHelper(){
        if (connection != null && !getIsClosed()) {
            try {
                getParticipants().clear();
                setIsClosed(true);
                connection.send(new ChatParticipant(getMyName())); // informs the allClientsMaintainer
                // through the singleClientHandler that this client should be removed
                connection.send(Client.TERMINATE); // tells the backGroundClient to stop running
                return true;
            } catch (IOException exception) {
                return true;
            }
        } return false;
    }

    private void closeSocketAndStreams() throws IOException {
        connection.getOutputObjectStream().close();
        connection.getInputObjectStream().close();
        connection.getSocket().close();
    }

    private void closeConnection() throws IOException {
        displayArea.setText(DEFAULT_TEXT);
        participantsArea.setText(DEFAULT_TEXT);
        displayMsg(CLOSING_CONNECTION_MSG);
        setTextFieldEditable(false);
        closeSocketAndStreams();
        this.setTitle(DEFAULT_FRAME_TITLE);
        displayMsg(CLOSED_MSG);
        setIsClosed(true);
    }

    private void updateParticipantsArea(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                participantsArea.setText(DEFAULT_TEXT);
                participantsArea.append(String.format(CHAT_PARTICIPANTS_TITLE, participants.size() ));
                for (ChatParticipant chatParticipant : participants.values()){
                    participantsArea.append(chatParticipant.toString());
                }
            }
        });
    }

    private void displayMsg(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                displayArea.append(msg);
            }
        });
    }

    private void setTextFieldEditable(final boolean isEditable){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        enterField.setEditable(isEditable);
                    }
                }
        );
    }
}