package chat_room;

import java.io.Serializable;
import java.util.Objects;

public class ChatParticipant implements Serializable {
    /*
    An object which plays a role of signal: when reading an instance of this class, this indicates that a
    new client has joined or an existing client has left. In this case the client with this instance name
    should be added to the list of clients of the allClientsMaintainer object, and also should be added to
    the chat participants area of each client (this is done through each client's singleClientHandler
    object which reads this instance.
     */
    private final String name;

    @Override
    public String toString() {
        return name + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatParticipant that = (ChatParticipant) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getName() {
        return name;
    }

    public ChatParticipant(String name){
        this.name = name;
    }
}