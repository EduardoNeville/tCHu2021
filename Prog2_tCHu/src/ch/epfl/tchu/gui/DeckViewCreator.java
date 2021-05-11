package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;


import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.awt.*;


class DeckViewCreator {

    private Node nodeForCard(Card card){


        Rectangle outside = new Rectangle(60,90);


        Rectangle inside = new Rectangle(40,70);
        inside.getStyleClass().add("inside");

        Rectangle image = new Rectangle();
        image.getStyleClass().add("train-image");

        StackPane cardColour = new StackPane(outside, inside);
        cardColour.getStyleClass().addAll(card.name(),"card");

        return cardColour;
    }

    public HBox createHandView(ObservableGameState observableGameState) {
        //add Compteur here

        HBox innerBox = new HBox();
        innerBox.setId("hand-pane");
        //ObservableGameState for it to get the dif StackPane

        ListView ticketListView = new ListView();//TODO Tickets for playerId of the current hand
        ticketListView.setId("tickets");
        HBox theBigBox = new HBox(ticketListView,innerBox);
        theBigBox.getStylesheets().addAll("decks.css","color.css");

        return theBigBox;
    }

    public void createCardsView(PlayerState playerState, CardState cardState, CardState ticketState) {
        Button cardButton = new Button();
        cardButton.getStyleClass().add("gauged");

        Button ticketsButton = new Button();
        ticketsButton.getStyleClass().add("gauged");



        VBox bigVBox = new VBox();
        bigVBox.getStylesheets().addAll("decks.css", "color.css");
    }

}
