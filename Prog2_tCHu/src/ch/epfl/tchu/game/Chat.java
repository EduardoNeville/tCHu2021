package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.ActionHandler;
import ch.epfl.tchu.net.ChatMessage;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public final class Chat {
    private static final ArrayBlockingQueue<ChatMessage> messagesOutgoing = new ArrayBlockingQueue<>(1);


    public static void run(Map<PlayerId, ChatUser> players, Map<PlayerId, String> playerNames) throws InterruptedException {

        ActionHandler.ChatHandler chatHandler = messagesOutgoing::add;
        players.values().forEach(p -> {
            p.receiveChatMessageHandler(chatHandler);
        }); //pass buffered writer????


        for(;;){ //TODO: add end condition
            ChatMessage msg = messagesOutgoing.take();
            players.values().forEach(p -> p.receiveChatMessage(msg));
        }

    }
}
