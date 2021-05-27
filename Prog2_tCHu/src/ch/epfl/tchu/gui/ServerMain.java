package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import static ch.epfl.tchu.game.PlayerId.*;

import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A tchu application which runs a tchu game. Serves as a server and runs a player interface for the local player.
 *
 * @author Martin Sanchez Lopez (313238)
 */
public final class ServerMain extends Application {

    /**
     * Java main method.
     * @param args optional : two names for the players
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Starts the application. Creates a server, waits for a connection and then
     * initializes a tchu game with two players, a local one and the one connected to this server via a proxy.
     *
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        List<String> args = getParameters().getRaw();
        Preconditions.checkArgument(args.size() == 0 || args.size() == 2);
        List<String> names;
        if(args.size() == 0)
            names = List.of("Ada", "Charles");
        else
            names = List.of(args.get(0), args.get(1));

        ServerSocket serverSocket = new ServerSocket( 5108);
        Socket socket = serverSocket.accept();

        RemotePlayerProxy proxy = new RemotePlayerProxy(socket);
        GraphicalPlayerAdapter localPlayer = new GraphicalPlayerAdapter();

        Map<PlayerId, Player> players = Map.of(PLAYER_1, localPlayer, PLAYER_2, proxy);
        Map<PlayerId, String> nameMap = Map.of(PLAYER_1, names.get(0), PLAYER_2, names.get(1));

        new Thread(() -> Game.play(players, nameMap, SortedBag.of(ChMap.tickets()), new Random()));
    }

}
