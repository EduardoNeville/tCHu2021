package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class RemotePlayerProxy implements Player {
    BufferedReader reader;
    BufferedWriter writer;

    public RemotePlayerProxy(Socket socket) throws IOException {
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.US_ASCII)); //TODO
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
            return String.join(" " ,reader.read());
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

        String playernames = Serdes.LIST_String_SERDE.serialize(List.copyOf(playerNames.values()));

        sendMessage(MessageId.INIT_PLAYERS.name(),playernames);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String info = Serdes.SORTED_BAG_TICKET_SERDE.serialize(tickets);

        sendMessage(MessageId.SET_INITIAL_TICKETS.name(),info);
    }

    @Override
    public void receiveInfo(String info) {
        String infom = Serdes.STRING_SERDE.serialize(info);

        sendMessage(MessageId.RECEIVE_INFO.name(),infom);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String publicGameState = Serdes.PUBLIC_GAME_STATE_SERDES.serialize(newState);
        String playerState = Serdes.PLAYER_STATE_SERDE.serialize(ownState);

        sendMessage(MessageId.UPDATE_STATE.name(),publicGameState,playerState);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(MessageId.CHOOSE_INITIAL_TICKETS.name());
        return Serdes.SORTED_BAG_TICKET_SERDE.deserialize(receiveMessage());
    }

    @Override
    public TurnKind nextTurn() {

        sendMessage(MessageId.NEXT_TURN.name());
        return Serdes.TURN_KIND_SERDE.deserialize(receiveMessage());
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        sendMessage(MessageId.CHOOSE_TICKETS.name());

        return Serdes.SORTED_BAG_TICKET_SERDE.deserialize(receiveMessage());
    }

    @Override
    public int drawSlot() {
        sendMessage(MessageId.DRAW_SLOT.name());

        return Serdes.INTEGER_SERDE.deserialize(receiveMessage());
    }

    @Override
    public Route claimedRoute() {
        sendMessage(MessageId.ROUTE.name());
        return Serdes.ROUTE_SERDE.deserialize(receiveMessage());
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(MessageId.CARDS.name());
        return null;
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {

        sendMessage(MessageId.CHOOSE_ADDITIONAL_CARDS.name());
        return null;
    }
}
