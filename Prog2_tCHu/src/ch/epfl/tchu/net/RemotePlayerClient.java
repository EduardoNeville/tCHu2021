package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;

import static ch.epfl.tchu.net.MessageId.CHAT_MESSAGE;
import static ch.epfl.tchu.net.Serdes.*;
import static ch.epfl.tchu.game.PlayerId.*;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Remote player client that connects a player to the proxy on the server to give the players actions.
 */
public final class RemotePlayerClient {
    private final ChatUser player;
    private final String address;
    private final int port;
    private BufferedReader reader;
    private BufferedWriter writer;
    private final BlockingQueue<String[]> gameMessage = new ArrayBlockingQueue<>(1);
//    private final BlockingQueue<String> chatMessage;

    /**
     * Constructs a remote player client.
     *
     * @param player  player associated with this client
     * @param address address of the proxy server
     * @param port    port of the proxy server
     */
    public RemotePlayerClient(ChatUser player, String address, int port) {
        this.player = Objects.requireNonNull(player);
        this.address = address;
        this.port = port;
//        this.chatMessage = chatMessages;
    }


    /**
     * Runs the client player which will listens for the proxy's messages and responds accordingly.
     */
    public void run() {
        try (Socket s = new Socket(address, port);
             BufferedReader r =
                     new BufferedReader(
                             new InputStreamReader(s.getInputStream(),
                                     US_ASCII));
             BufferedWriter w =
                     new BufferedWriter(
                             new OutputStreamWriter(s.getOutputStream(),
                                     US_ASCII))) {
            //problem with threads other wise it seemed
            reader = r;
            writer = w;
            player.receiveChatMessageHandler(m -> {
                try {
                    writer.write(CHAT_MESSAGE.name() + " " + CHAT_MESSAGE_SERDE.serialize(m));
                    writer.write("\n");
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            new Thread(() -> {
                while(true) {
                    try {
                        String line;
                        if ((line = reader.readLine()) != null) {
                            String[] messageReceived = line.split(Pattern.quote(" "), -1);
                            if (messageReceived[0].equals(CHAT_MESSAGE.name())) {
                                player.receiveChatMessage(CHAT_MESSAGE_SERDE.deserialize(messageReceived[1]));
                            } else
                                gameMessage.put(messageReceived);
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            while(true){
                String[] message = gameMessage.take();
                String playerResponse = response(message);
                if (playerResponse != null) {
                    w.write(playerResponse);
                    w.write("\n");
                    w.flush();
                }
            }


        } catch (IOException | InterruptedException e) {
            throw new UncheckedIOException((IOException) e);
        }
    }

    /**
     * Returns a serialized response if the game expects a response, null if no response is needed.
     *
     * @param message server's message
     * @return a serialized response if the game expects a response, null if no response is needed
     */
    private String response(String[] message) {
        MessageId msgId = MessageId.valueOf(message[0]);

        switch (msgId) {
            case INIT_PLAYERS:
                String[] names = message[2].split(Pattern.quote(","), -1);
                Map<PlayerId, String> map = Map.of(PLAYER_1, STRING_SERDE.deserialize(names[0]),
                        PLAYER_2, STRING_SERDE.deserialize(names[1]));
                player.initPlayers(PLAYER_ID_SERDE.deserialize(message[1]), map);
                return null;
            case RECEIVE_INFO:
                player.receiveInfo(STRING_SERDE.deserialize(message[1]));
                return null;
            case UPDATE_STATE:
                player.updateState(
                        PUBLIC_GAME_STATE_SERDE.deserialize(message[1]),
                        PLAYER_STATE_SERDE.deserialize(message[2])
                );
                return null;
            case SET_INITIAL_TICKETS:
                player.setInitialTicketChoice(
                        SORTED_BAG_TICKET_SERDE.deserialize(message[1])
                );
                return null;
            case CHOOSE_INITIAL_TICKETS:
                SortedBag<Ticket> initialTicketBag = player.chooseInitialTickets();
                return SORTED_BAG_TICKET_SERDE.serialize(initialTicketBag);
            case NEXT_TURN:
                Player.TurnKind turnKind = player.nextTurn();
                return TURN_KIND_SERDE.serialize(turnKind);
            case CHOOSE_TICKETS:
                SortedBag<Ticket> ticketBag = player.chooseTickets(SORTED_BAG_TICKET_SERDE.deserialize(message[1]));
                return SORTED_BAG_TICKET_SERDE.serialize(ticketBag);
            case DRAW_SLOT:
                int slot = player.drawSlot();
                return INTEGER_SERDE.serialize(slot);
            case ROUTE:
                Route route = player.claimedRoute();
                return ROUTE_SERDE.serialize(route);
            case CARDS:
                SortedBag<Card> cardBag = player.initialClaimCards();
                return SORTED_BAG_CARD_SERDE.serialize(cardBag);
            case CHOOSE_ADDITIONAL_CARDS:
                SortedBag<Card> additionalCardBag = player.chooseAdditionalCards(
                        LIST_SORTED_BAG_CARD_SERDE.deserialize(message[1])
                );
                return SORTED_BAG_CARD_SERDE.serialize(additionalCardBag);
            default:
                throw new Error();
        }
    }

}
