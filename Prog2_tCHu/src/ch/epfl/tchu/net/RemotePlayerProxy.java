package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * RemotePlayerProxy
 *
 * @author Eduardo Neville (314677)
 */
public class RemotePlayerProxy implements Player {
    BufferedReader reader;
    BufferedWriter writer;

    /**
     * RemotePlayerProxy
     * @param socket Input of the player
     * @throws IOException If the input/ output is wrong
     */
    public RemotePlayerProxy(Socket socket) throws IOException {
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.US_ASCII));
    }

    private void sendMessage(String...argument){
        String fnna = String.join(" ", argument) + "\n";

        try{
                writer.write(fnna);
                writer.flush();
                }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    private String receiveMessage(){
        try{
            return String.join(" ", reader.readLine());
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    /**
     * initPlayers
     * @param ownId
     *          id of the player
     * @param playerNames names of players
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

        String playernames = Serdes.LIST_String_SERDE.serialize(List.copyOf(playerNames.values()));

        sendMessage(MessageId.INIT_PLAYERS.name(),playernames);
    }

    /**
     * setInitialTicketChoice tickets at the beginning
     * @param tickets tickets of the player at the beginning
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String info = Serdes.SORTED_BAG_TICKET_SERDE.serialize(tickets);

        sendMessage(MessageId.SET_INITIAL_TICKETS.name(),info);
    }

    /**
     * receiveInfo Serialized version of the information given
     * @param info information in the form of String
     */
    @Override
    public void receiveInfo(String info) {
        String infom = Serdes.STRING_SERDE.serialize(info);

        sendMessage(MessageId.RECEIVE_INFO.name(),infom);
    }

    /**
     * updateState Updates the state in a serialized way
     * @param newState
     *          new public game state
     * @param ownState PlayerState of the player at hand
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String publicGameState = Serdes.PUBLIC_GAME_STATE_SERDES.serialize(newState);
        String playerState = Serdes.PLAYER_STATE_SERDE.serialize(ownState);

        sendMessage(MessageId.UPDATE_STATE.name(),publicGameState,playerState);
    }

    /**
     * chooseInitialTickets
     * @return Deserialised version of the chosen initial tickets
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(MessageId.CHOOSE_INITIAL_TICKETS.name());
        return Serdes.SORTED_BAG_TICKET_SERDE.deserialize(receiveMessage());
    }

    /**
     * nextTurn
     * @return Deserializes the messages for the next turn
     */
    @Override
    public TurnKind nextTurn() {

        sendMessage(MessageId.NEXT_TURN.name());
        return Serdes.TURN_KIND_SERDE.deserialize(receiveMessage());
    }

    /**
     * chooseTickets
     * @param options
     *          the tickets the player can choose from
     * @return Deserializes the chosen possible tickets the player can choose from
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        sendMessage(MessageId.CHOOSE_TICKETS.name());

        return Serdes.SORTED_BAG_TICKET_SERDE.deserialize(receiveMessage());
    }

    /**
     * drawSlot
     * @return Deserialized slot
     */
    @Override
    public int drawSlot() {
        sendMessage(MessageId.DRAW_SLOT.name());

        return Serdes.INTEGER_SERDE.deserialize(receiveMessage());
    }

    /**
     * claimedRoute
     * @return Deserialized claimed routes by player
     */
    @Override
    public Route claimedRoute() {
        sendMessage(MessageId.ROUTE.name());
        return Serdes.ROUTE_SERDE.deserialize(receiveMessage());
    }

    /**
     * initialClaimCards
     * @return Deserialized SortedBag of initial player claimed cards
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(MessageId.CARDS.name());
        return Serdes.SORTED_BAG_CARD_SERDE.deserialize(receiveMessage());
    }

    /**
     * chooseAdditionalCards
     * @param options
     *          list of options the player can choose from to finalise claim
     * @return Deserialized SortedBag of chosen additional cards by player
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {

        sendMessage(MessageId.CHOOSE_ADDITIONAL_CARDS.name());
        return Serdes.SORTED_BAG_CARD_SERDE.deserialize(receiveMessage());
    }
}
