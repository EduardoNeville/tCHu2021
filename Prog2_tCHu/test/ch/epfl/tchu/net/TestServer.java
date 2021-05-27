package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.Info;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public class TestServer {
    public static void main(String[] args) throws IOException {

        GameState state = GameState.initial(SortedBag.of(ChMap.tickets()), new Random(2021));
        System.out.println("S: Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
            Player playerProxy = new RemotePlayerProxy(socket);
            var playerNames = Map.of(PLAYER_1, "Ada",
                    PLAYER_2, "Charles");
            playerProxy.initPlayers(PLAYER_1, playerNames);
            playerProxy.setInitialTicketChoice(SortedBag.of(1, ChMap.tickets().get(0), 1, ChMap.tickets().get(1)));
            var chosenTickets = playerProxy.chooseInitialTickets();
            System.out.println("S: initial tickets : " +  chosenTickets.get(0).text() + chosenTickets.get(1).text());
            playerProxy.receiveInfo(new Info("Ada").keptTickets(2));
            playerProxy.updateState(state, state.playerState(PLAYER_1));
            System.out.println(playerProxy.nextTurn());
            var ticks = playerProxy.chooseTickets(SortedBag.of(1, ChMap.tickets().get(2), 1, ChMap.tickets().get(3)));
            System.out.println("S: initial tickets : " +  ticks.get(0).text() + ticks.get(1).text());
            System.out.println(playerProxy.drawSlot());
            System.out.println(playerProxy.claimedRoute().stations());
            System.out.println(playerProxy.initialClaimCards().toString());
            System.out.println(playerProxy.chooseAdditionalCards(List.of(SortedBag.of(Card.LOCOMOTIVE), SortedBag.of())));

        }
        System.out.println("S: Server done!");
    }
}
