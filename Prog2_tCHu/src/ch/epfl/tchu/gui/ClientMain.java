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
 * A tchu application which connects to a remote tchu game.
 * Connect to the server and runs a player interface for the local player.
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
     * Starts the application. Tries to connect to the server and then
     * initializes the gui for the local player.
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

        GraphicalPlayerAdapter localPlayer = new GraphicalPlayerAdapter();
        RemotePlayerClient client = new RemotePlayerClient(localPlayer, hostname, port);

        new Thread((client::run)).start();
    }

}
