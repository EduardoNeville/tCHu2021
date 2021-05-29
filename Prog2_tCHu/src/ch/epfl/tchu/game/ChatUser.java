package ch.epfl.tchu.game;

import ch.epfl.tchu.gui.ActionHandler;
import ch.epfl.tchu.net.ChatMessage;

public interface ChatUser extends Player{


    /**
     * Communicates to the player a new chat message.
     * @param message
     */
    void receiveChatMessage(ChatMessage message);


    void receiveChatMessageHandler(ActionHandler.ChatHandler chatHandler);
}
