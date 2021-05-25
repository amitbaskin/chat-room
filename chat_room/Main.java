package chat_room;

import chat_room.client.Client;
import chat_room.server.Server;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The running class of this chat room simulation
 */
public abstract class Main {
    private static final int INITIAL_CLIENTS_AMOUNT = 3;

    /**
     * The running method
     * @param args The arguments of this program (ignored)
     */
    public static void main(String[] args) {
        run();
    }

    /**
     * Running the program
     */
    public static void run(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        new Server(executorService);
        for (int i = 0; i < INITIAL_CLIENTS_AMOUNT; i++) {
            new Client();
        }
    }
}