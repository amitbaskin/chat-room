package chat_room.server;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ParticipantsLimitField extends JTextField {
    private static final String FOCUS_GONE_DEFAULT_TEXT = "";
    private static final String LIMIT_REQUEST = "Set participants limit (it is currently %d)";

    public ParticipantsLimitField(final Server server){
        setText(String.format(LIMIT_REQUEST, server.getMaxParticipantsAmount()));
        setEditable(true);
        addActionListener(new ParticipantsLimitFieldListener(server));
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        setText(FOCUS_GONE_DEFAULT_TEXT);
                    }
                });
            }
            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        setText(String.format(LIMIT_REQUEST, server.getMaxParticipantsAmount()));
                    }
                });
            }
        });
    }
}