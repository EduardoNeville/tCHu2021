package ch.epfl.tchu.net;

import ch.epfl.tchu.game.PlayerId;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class ChatMessage {
    private final String message;
    private final String time;
    private final PlayerId sender;

    public ChatMessage(String message, PlayerId sender) {
        this.message = message + "\n";
        Calendar c = Calendar.getInstance();
        this.time = String.format("%tl:%tM", c, c);
        this.sender = sender;
    }

    public PlayerId senderId(){
        return sender;
    }


    @Override
    public String toString() {
        return time + ": " + message;
    }
}
