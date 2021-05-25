package chat_room;

import chat_room.client.Client;
import chat_room.server.Server;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Main {
    private static final int INITIAL_CLIENTS_AMOUNT = 3;

    public static void main(String[] args) {
        run();
    }

    public static void run(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        new Server(executorService);
        for (int i = 0; i < INITIAL_CLIENTS_AMOUNT; i++) {
            new Client();
        }
    }
}