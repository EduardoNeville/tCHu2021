package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

public class InfoViewCreator {



    private static TextFlow playerStats(PlayerId playerId, String name, ObservableGameState observableGameState){
        TextFlow textFlow = new TextFlow();
        textFlow.getStyleClass().add(playerId.name());

        Circle circle = new Circle();
        circle.setRadius(5d);
        circle.getStyleClass().add("filled");

        Text statsText = new Text();
        statsText.textProperty().bind(
                Bindings.format(
                        StringsFr.PLAYER_STATS,
                        name,
                        observableGameState.getPlayerTicketCount(playerId),
                        observableGameState.getPlayerCardCount(playerId),
                        observableGameState.getPlayerCarCount(playerId),
                        observableGameState.getPlayerConstructionPoints(playerId)));

        textFlow.getChildren().addAll(circle, statsText);

        return textFlow;
    }




    public static Node createInfoView(PlayerId idOfUser,
                               Map<PlayerId, String> playerNames,
                               ObservableGameState oGameState,
                               ObservableList<Text> infoList){


        VBox topNode = new VBox();
        topNode.getStylesheets().addAll("info.css", "colors.css");

        VBox playerStats = new VBox();
        playerStats.setId("player-stats");
        playerStats.getChildren().add(
                playerStats(idOfUser, playerNames.get(idOfUser), oGameState));
        PlayerId otherId = idOfUser.next();
        playerStats.getChildren().add(
            playerStats(otherId, playerNames.get(otherId), oGameState));


        TextFlow messages = new TextFlow();
        messages.setId("game-info");
        Bindings.bindContent(messages.getChildren(), infoList);

        topNode.getChildren().addAll(playerStats, new Separator(), messages);

        return topNode;
    }
}
