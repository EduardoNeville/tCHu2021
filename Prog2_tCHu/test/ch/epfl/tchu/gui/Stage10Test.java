package ch.epfl.tchu.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.ActionHandler.*;
import ch.epfl.tchu.game.*;
//TODO
//TODO

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.*;

import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static javafx.application.Application.launch;

public class Stage10Test extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    private void setState(GraphicalPlayer player) {
        ObservableGameState gameState = new ObservableGameState(PLAYER_1);


        PlayerState p1State =
                new PlayerState(SortedBag.of(ChMap.tickets().subList(0, 3)),
                        SortedBag.of(1, Card.WHITE, 3, Card.RED),
                        ChMap.routes().subList(0, 3));

        PublicPlayerState p2State =
                new PublicPlayerState(0, 0, ChMap.routes().subList(3, 6));


        Map<PlayerId, PublicPlayerState> pubPlayerStates =
                Map.of(PLAYER_1, p1State, PLAYER_2, p2State);
        PublicCardState cardState =
                new PublicCardState(Card.ALL.subList(0, 5), 110 - 2 * 4 - 5, 0);
        PublicGameState publicGameState =
                new PublicGameState(36, cardState, PLAYER_1, pubPlayerStates, null);
        gameState.setState(publicGameState, p1State);


        player.setState(publicGameState, p1State);
    }

    @Override
    public void start(Stage primaryStage) {
        Map<PlayerId, String> playerNames =
                Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");

        ChatHandler chatHandler = System.out::println;
        GraphicalPlayer p = new GraphicalPlayer(PLAYER_1, playerNames, chatHandler);
        setState(p);

        DrawTicketsHandler drawTicketsH =
                () -> p.receiveInfo("Je tire des billets !");
        DrawCardHandler drawCardH =
                s -> p.receiveInfo(String.format("Je tire une carte de %s !", s));
        ClaimRouteHandler claimRouteH =
                (r, cs) -> {
                    String rn = r.station1() + " - " + r.station2();
                    p.receiveInfo(String.format("Je m'empare de %s avec %s", rn, cs));
                };

        p.startTurn(drawTicketsH, drawCardH, claimRouteH, null);
    }
}
