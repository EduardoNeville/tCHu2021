package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.ActionHandler;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;


public class ClientTest {

    public static void main(String[] args) {
        System.out.println("Starting client!");
        RemotePlayerClient playerClient =
                new RemotePlayerClient(new TestPlayer(),
                        "localhost",
                        5108);
        playerClient.run();
        System.out.println("Client done!");
    }

    private final static class TestPlayer implements ChatUser {
        private SortedBag<Ticket> initTickets= SortedBag.of();


        @Override
        public void initPlayers(PlayerId ownId,
                                Map<PlayerId, String> names) {
            System.out.printf("ownId: %s\n", ownId);
            System.out.printf("playerNames: %s\n", names);
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            tickets.forEach(s -> System.out.print(s.text()));
            System.out.println("\n");
            initTickets = tickets;
        }

        @Override
        public void receiveInfo(String info) {
            System.out.printf("info string: %s\n", info);
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            System.out.printf("new PGState: %s\n", newState);
            System.out.printf("new own PlayerState: %s\n", ownState);
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            System.out.print("choosing first 2 tickets of :");
            initTickets.forEach(s -> System.out.print(s.text()));
            System.out.println("\n");
            return SortedBag.of(1, initTickets.get(0), 1, initTickets.get(1));
        }

        @Override
        public TurnKind nextTurn() {
            return TurnKind.CLAIM_ROUTE;
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            System.out.printf("choosing first too of %s\n", options);
            return SortedBag.of(1, options.get(0),1, options.get(1));
        }

        @Override
        public int drawSlot() {
            System.out.println("returning slot 3");
            return 3;
        }

        @Override
        public Route claimedRoute() {
            return ChMap.routes().get(1);
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            return SortedBag.of(1, Card.RED);
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            return options.get(0);
        }

        @Override
        public void receiveChatMessage(ChatMessage message) {

        }

        @Override
        public void receiveChatMessageHandler(ActionHandler.ChatHandler chatHandler) {

        }


        // … autres méthodes de Player
    }
}
