package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import static ch.epfl.tchu.net.Serdes.*;
import static ch.epfl.tchu.net.MessageId.*;

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
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII)); //TODO
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
//            return String.join(" " ,reader.read());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

//        String playernames = LIST_String_SERDE.serialize(List.copyOf(playerNames.values()));
//      TODO maybe easier way?
        List<String> serializedNames = List.of((playerNames.get(PlayerId.PLAYER_1)),
                (playerNames.get(PlayerId.PLAYER_2)));
        String serializedList = LIST_String_SERDE.serialize(serializedNames);
        String id = PLAYER_ID_SERDE.serialize(ownId);

        sendMessage(INIT_PLAYERS.name(), id, serializedList);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String info = SORTED_BAG_TICKET_SERDE.serialize(tickets);

        sendMessage(SET_INITIAL_TICKETS.name(), info);
    }

    @Override
    public void receiveInfo(String info) {
        String infom = STRING_SERDE.serialize(info);

        sendMessage(RECEIVE_INFO.name(), infom);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String publicGameState = PUBLIC_GAME_STATE_SERDE.serialize(newState);
        String playerState = PLAYER_STATE_SERDE.serialize(ownState);

        sendMessage(UPDATE_STATE.name(), publicGameState, playerState);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(CHOOSE_INITIAL_TICKETS.name());
        return SORTED_BAG_TICKET_SERDE.deserialize(receiveMessage());
    }

    @Override
    public TurnKind nextTurn() {

        sendMessage(NEXT_TURN.name());
        return TURN_KIND_SERDE.deserialize(receiveMessage());
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        sendMessage(CHOOSE_TICKETS.name(), SORTED_BAG_TICKET_SERDE.serialize(options));

        return SORTED_BAG_TICKET_SERDE.deserialize(receiveMessage());
    }

    @Override
    public int drawSlot() {
        sendMessage(DRAW_SLOT.name());

        return INTEGER_SERDE.deserialize(receiveMessage());
    }

    @Override
    public Route claimedRoute() {
        sendMessage(ROUTE.name());
        return ROUTE_SERDE.deserialize(receiveMessage());
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(CARDS.name());
        return SORTED_BAG_CARD_SERDE.deserialize(receiveMessage());
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {

        sendMessage(CHOOSE_ADDITIONAL_CARDS.name(), LIST_SORTED_BAG_CARD_SERDE.serialize(options));
        return SORTED_BAG_CARD_SERDE.deserialize(receiveMessage());
    }
}
