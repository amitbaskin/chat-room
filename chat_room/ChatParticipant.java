package chat_room;

import java.io.Serializable;
import java.util.Objects;

/**
 An object which plays a role of signal: when reading an instance of this class, this indicates that a
 new client has joined or an existing client has left. In this case the client with this instance name
 should be added to the list of clients of the allClientsMaintainer object, and also should be added to
 the chat participants area of each client (this is done through each client's singleClientHandler
 object which reads this instance.
 */
public class ChatParticipant implements Serializable {
    private final String name;

    /**
     * Create a new chat-participant
     * @param name The name of this participant
     */
    public ChatParticipant(String name){
        this.name = name;
    }

    /**
     * Get the name of this participant
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Used for displaying the name
     * @return The name for display
     */
    @Override
    public String toString() {
        return name + "\n";
    }

    /**
     * Two participants are equal if they have the same name
     * @param other The participant to compare with
     * @return True iff they are equal
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ChatParticipant that = (ChatParticipant) other;
        return Objects.equals(name, that.name);
    }

    /**
     * Gets hash code according to this participants name
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}