package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import static ch.epfl.tchu.game.PlayerId.*;

import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerClient;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *                A tchu application which runs a tchu game. Serves as a server and runs a player interface for the local player.
 *
 * @author Martin Sanchez Lopez (313238)
 */
public final class ClientMain extends Application {

    /**
     * Java main method.
     * @param args optional : the hostname and port
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     *          S    tarts the application. Creates a server, waits for a connection and then
     *                  initializes a tchu game with two players, a local one and the one connected to this server via a proxy.
     *
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        List<String> args = getParameters().getRaw();
        Preconditions.checkArgument(args.size() == 0 || args.size() == 2);
        String hostname;
        int port;
        if(args.size() == 0){
            hostname = "localhost";
            port = 5108;
        }
        else{
            hostname = args.get(0);
            port = Integer.parseInt(args.get(1));
        }

        System.out.println(hostname + " " + port);

        GraphicalPlayerAdapter localPlayer = new GraphicalPlayerAdapter();
        RemotePlayerClient client = new RemotePlayerClient(localPlayer, hostname, port);
        System.out.println("connceted");

        new Thread((client::run)).start();
    }

}
