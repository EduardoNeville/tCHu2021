package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.Group;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;

import java.util.List;

/**
 * Private class DecksViewCreator
 *
 * @author Eduardo Neville (314667)
 */
class DecksViewCreator {

    private static StackPane nodeForCard(Card card){
        Rectangle outside = new Rectangle(60,90);

        Rectangle inside = new Rectangle(40,70);
        inside.getStyleClass().add("inside");

        Rectangle image = new Rectangle(40,70);
        image.getStyleClass().add("train-image");

        StackPane cardColour = new StackPane(outside, inside);


        if (card != null)
            if (!card.name().equals(Card.LOCOMOTIVE.name()))
                cardColour.getStyleClass().addAll(card.name(), "card");
            else
                cardColour.getStyleClass().addAll("NEUTRAL", "card");
        else
            cardColour.getStyleClass().addAll("", "card");

        return cardColour;
    }

    public static HBox createHandView(ObservableGameState observableGameState){

        HBox innerBox = new HBox();
        innerBox.setId("hand-pane");

        for (Card cardInLoop: Card.ALL) {
            ReadOnlyIntegerProperty count = observableGameState.getPlayerCardsCount(cardInLoop);
            StackPane card = nodeForCard(cardInLoop);
            card.visibleProperty().bind(Bindings.greaterThan(count, 0));
            Text counter = new Text();

            counter.getStyleClass().add("count");
            //TODO verify that asString works if not use
            //Bindings.convert(observableGameState.getPlayerCardsCount(cardInLoop));
            counter.textProperty().bind(observableGameState.getPlayerCardsCount(cardInLoop).asString());
        }

        ListView<Ticket> ticketListView = new ListView<>();
        ticketListView.setId("tickets");

        HBox theBigBox = new HBox(ticketListView,innerBox);
        theBigBox.getStylesheets().addAll("decks.css","colors.css");

        return theBigBox;
    }

    public static VBox createCardsView(ObservableGameState observableGameState,
                                        ObjectProperty<ActionHandler.DrawTicketsHandler> drawTicketsHandlerObjectProperty,
                                        ObjectProperty<ActionHandler.DrawCardHandler> drawCardHandlerObjectProperty){

        Button cardButton = sideButton(StringsFr.CARDS,observableGameState.cardAmountProperty());
        cardButton.getStyleClass().add("gauged");

        Button ticketsButton = sideButton(StringsFr.TICKETS,observableGameState.ticketAmountProperty());
        ticketsButton.getStyleClass().add("gauged");
        VBox faceUpCardVBox = new VBox();

        for (int i : Constants.FACE_UP_CARD_SLOTS) {
            StackPane card = nodeForCard(null);
            observableGameState.faceUpCard(i).addListener((observableValue,old,newValue) ->{
                if (!newValue.name().equals(Card.LOCOMOTIVE.name()))
                    card.getStyleClass().set(0,newValue.name());
                else
                    card.getStyleClass().set(0,"NEUTRAL");});
            faceUpCardVBox.getChildren().add(card);
        }

        cardButton.setOnMouseClicked(e -> drawCardHandlerObjectProperty.get().onDrawCard(Constants.DECK_SLOT));
        ticketsButton.setOnMouseClicked(e -> drawTicketsHandlerObjectProperty.get().onDrawTickets());

        faceUpCardVBox.getStylesheets().addAll("decks.css", "colors.css");
        return faceUpCardVBox;
    }

    private static Button sideButton(String name, ReadOnlyIntegerProperty percentage){

        Rectangle gaugeForeground = new Rectangle(50,5);
        gaugeForeground.getStyleClass().add("foreground");
        Rectangle gaugeBackground = new Rectangle(50,5);
        gaugeForeground.getStyleClass().add("background");

        gaugeForeground.widthProperty().bind(percentage.multiply(50).divide(100));

        Group grounds = new Group(gaugeBackground,gaugeForeground);
        Button theButton = new Button(name);
        theButton.setGraphic(grounds);
        return theButton;
    }
}
