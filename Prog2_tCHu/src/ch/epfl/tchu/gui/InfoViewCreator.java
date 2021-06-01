package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.ChatMessage;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

/**
 * InfoViewCreator class
 *
 * @author Martin Sanchez Lopez (313238)
 */
public final class InfoViewCreator {

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

    /**
     * Returns a Node with the information that can be viewed
     * @param idOfUser Id of the User
     * @param playerNames Names of the players
     * @param oGameState State of the game from the perspective of the observable player
     * @param infoList observable list of information
     * @return Node with the information that can be viewed
     */
    public static Node createInfoView(PlayerId idOfUser,
                                      Map<PlayerId, String> playerNames,
                                      ObservableGameState oGameState,
                                      ObservableList<Text> infoList,
                                      ObservableList<ChatMessage> chatList,
                                      StringProperty outGoingMessages,
                                      ObjectProperty<ActionHandler.ChatHandler> chatHandler){


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
        messages.setMaxWidth(170);
        Bindings.bindContent(messages.getChildren(), infoList);

        TextFlow chat = new TextFlow();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setLayoutY(10);
        scrollPane.setPrefWidth(170);
        scrollPane.setPrefHeight(150);
        scrollPane.setContent(chat);
        chat.setMaxWidth(40d);
        chat.getStyleClass().add("colors.css");
        chatList.addListener((ListChangeListener<ChatMessage>) change -> {
            if (change.next()){
                Circle circle = new Circle(5);
                circle.getStyleClass().add("filled");

                ChatMessage msg = chatList.get(chatList.size()-1); //latest message

                //Pane with the message text and colored circle
                TextFlow textFlow = new TextFlow(circle, new Text(" " + msg.toString()));
                textFlow.setMaxWidth(170);
                textFlow.setId("chat-element");
                textFlow.getStyleClass().addAll(msg.senderId().name());

                chat.getChildren().addAll(textFlow);

                scrollPane.layout(); //to refresh v max
                scrollPane.setVvalue(scrollPane.getVmax()); //scroll to bottom
            }
        });

        TextField textField = new TextField();
        messages.setId("chat-input");
        outGoingMessages.bind(textField.textProperty());
        textField.setOnAction(e -> {
            chatHandler.get().onSend(new ChatMessage(textField.getText(), idOfUser));
            textField.setText(null);
        });




        topNode.getChildren().addAll(playerStats, new Separator(), messages, new Separator(), scrollPane, textField);
        return topNode;
    }

}