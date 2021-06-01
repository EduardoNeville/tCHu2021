package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.ActionHandler;

import static ch.epfl.tchu.net.Serdes.*;
import static ch.epfl.tchu.net.MessageId.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * RemotePlayerProxy
 *
 * @author Eduardo Neville (314677)
 */
public class RemotePlayerProxy implements Player, ChatUser {
    BufferedReader reader;
    BufferedWriter writer;
    private final BlockingQueue<String> gameResponses = new ArrayBlockingQueue<>(1);

    /**
     * RemotePlayerProxy
     *
     * @param socket Input of the player
     * @throws IOException If the input/ output is wrong
     */
    public RemotePlayerProxy(Socket socket, BlockingQueue<ChatMessage> chatQueue) throws IOException {
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));


        new Thread(() -> {
            while (true) {
                String response = receiveMessage();
                if (response.contains(CHAT_MESSAGE.name())) {
                    try {
                        chatQueue.put(CHAT_MESSAGE_SERDE.deserialize(response.substring(CHAT_MESSAGE.name().length() + 1)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        gameResponses.put(response);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void sendMessage(String... argument) {
        String fnna = String.join(" ", argument) + "\n";

        try {
            writer.write(fnna);
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String receiveMessage() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * initPlayers
     *
     * @param ownId       id of the player
     * @param playerNames names of players
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

        List<String> serializedNames = List.of((playerNames.get(PlayerId.PLAYER_1)),
                (playerNames.get(PlayerId.PLAYER_2)));
        String serializedList = LIST_String_SERDE.serialize(serializedNames);
        String id = PLAYER_ID_SERDE.serialize(ownId);

        sendMessage(INIT_PLAYERS.name(), id, serializedList);
    }

    /**
     * setInitialTicketChoice tickets at the beginning
     *
     * @param tickets tickets of the player at the beginning
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String info = SORTED_BAG_TICKET_SERDE.serialize(tickets);

        sendMessage(SET_INITIAL_TICKETS.name(), info);
    }

    /**
     * receiveInfo Serialized version of the information given
     *
     * @param info information in the form of String
     */
    @Override
    public void receiveInfo(String info) {
        String infom = STRING_SERDE.serialize(info);

        sendMessage(RECEIVE_INFO.name(), infom);
    }

    /**
     * updateState Updates the state in a serialized way
     *
     * @param newState new public game state
     * @param ownState PlayerState of the player at hand
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String publicGameState = PUBLIC_GAME_STATE_SERDE.serialize(newState);
        String playerState = PLAYER_STATE_SERDE.serialize(ownState);

        sendMessage(UPDATE_STATE.name(), publicGameState, playerState);
    }


    /**
     * chooseInitialTickets
     *
     * @return Deserialised version of the chosen initial tickets
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(CHOOSE_INITIAL_TICKETS.name());
        return SORTED_BAG_TICKET_SERDE.deserialize(blockingQ(gameResponses));
    }

    /**
     * nextTurn
     *
     * @return Deserializes the messages for the next turn
     */
    @Override
    public TurnKind nextTurn() {

        sendMessage(NEXT_TURN.name());
        return TURN_KIND_SERDE.deserialize(blockingQ(gameResponses));
    }

    /**
     * chooseTickets
     *
     * @param options the tickets the player can choose from
     * @return Deserializes the chosen possible tickets the player can choose from
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        sendMessage(CHOOSE_TICKETS.name(), SORTED_BAG_TICKET_SERDE.serialize(options));

        return SORTED_BAG_TICKET_SERDE.deserialize(blockingQ(gameResponses));
    }

    /**
     * drawSlot
     *
     * @return Deserialized slot
     */
    @Override
    public int drawSlot() {
        sendMessage(DRAW_SLOT.name());

        return INTEGER_SERDE.deserialize(blockingQ(gameResponses));
    }

    /**
     * claimedRoute
     *
     * @return Deserialized claimed routes by player
     */
    @Override
    public Route claimedRoute() {
        sendMessage(ROUTE.name());
        return ROUTE_SERDE.deserialize(blockingQ(gameResponses));
    }

    /**
     * initialClaimCards
     *
     * @return Deserialized SortedBag of initial player claimed cards
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(CARDS.name());
        return SORTED_BAG_CARD_SERDE.deserialize(blockingQ(gameResponses));
    }

    /**
     * chooseAdditionalCards
     *
     * @param options list of options the player can choose from to finalise claim
     * @return Deserialized SortedBag of chosen additional cards by player
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {

        sendMessage(CHOOSE_ADDITIONAL_CARDS.name(), LIST_SORTED_BAG_CARD_SERDE.serialize(options));
        return SORTED_BAG_CARD_SERDE.deserialize(blockingQ(gameResponses));
    }

    @Override
    public TradeDeal makeTradeOffer() {
        return TRADE_DEAL_SERDE.deserialize(blockingQ(gameResponses));
    }

    @Override
    public boolean acceptTradeOffer(TradeDeal offer) {
        sendMessage(TRADE_DEAL_ACCEPT.name(), TRADE_DEAL_SERDE.serialize(offer));
        return BOOLEAN_SERDE.deserialize(blockingQ(gameResponses));
    }


    @Override
    public void receiveChatMessage(ChatMessage message) {
        sendMessage(CHAT_MESSAGE.name(), CHAT_MESSAGE_SERDE.serialize(message));
    }

    @Override
    public void receiveChatMessageHandler(ActionHandler.ChatHandler chatHandler) {

    }

    private <T> T blockingQ(BlockingQueue<T> blockingQueue) {
        try {
            return blockingQueue.take();
        } catch (InterruptedException e) {
            throw new IllegalArgumentException();
        }
    }

}
