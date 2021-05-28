package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import static ch.epfl.tchu.game.PlayerId.*;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Random;

public final class Stage11Test extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        Map<PlayerId, String> names =
                Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");

        GraphicalPlayerAdapter p1 = new GraphicalPlayerAdapter();
        GraphicalPlayerAdapter p2 = new GraphicalPlayerAdapter();
        Random rng = new Random();

        new Thread(() -> {
            try {
                Chat.run(Map.of(PLAYER_1, p1,
                        PLAYER_2, p2), names);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> Game.play(Map.of(PLAYER_1, p1,
                PLAYER_2, p2), names, tickets, rng))
                .start();
    }
}