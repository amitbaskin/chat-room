package chat_room.server;

import chat_room.Connection;

import java.io.IOException;

public class MsgSender implements Runnable{
    private final Connection dest;
    private final Object msg;

    public MsgSender(Connection dest, Object msg){
        this.dest = dest;
        this.msg = msg;
    }

    @Override
    public void run() {
        try {
            dest.send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
