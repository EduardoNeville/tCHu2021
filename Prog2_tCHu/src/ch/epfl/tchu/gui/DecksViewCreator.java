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

/**
 * Private class DecksViewCreator
 *
 * @author Eduardo Neville (314667)
 */
class DecksViewCreator {

    private static StackPane nodeForCard(Card card){
        Rectangle outside = new Rectangle(60,90);
        outside.getStyleClass().add("outside");


        Rectangle inside = new Rectangle(40,70);
        inside.getStyleClass().addAll("inside", "filled");

        Rectangle image = new Rectangle(40,70);
        image.getStyleClass().add("train-image");

        StackPane cardPane = new StackPane(outside, inside, image);


        if (card != null)
            if (!card.equals(Card.LOCOMOTIVE))
                cardPane.getStyleClass().addAll(card.color().name(), "card");
            else
                cardPane.getStyleClass().addAll("NEUTRAL", "card");
        else
            cardPane.getStyleClass().addAll("", "card");

        return cardPane;
    }

    /**
     * createHandView explained in @return
     * @param observableGameState the game state of the observed player
     * @return creates the HBox that represents the Cards and Tickets the observed player has
     */
    public static HBox createHandView(ObservableGameState observableGameState){

        HBox innerBox = new HBox();
        innerBox.setId("hand-pane");

        for (Card cardInLoop: Card.ALL) {
            StackPane card = nodeForCard(cardInLoop);

            Text counter = new Text();
            counter.getStyleClass().add("count");
            card.getChildren().addAll(counter);

            ReadOnlyIntegerProperty count = observableGameState.getPlayerCardsCount(cardInLoop);
            card.visibleProperty().bind(Bindings.greaterThan(count, 0));

            counter.textProperty().bind(observableGameState.getPlayerCardsCount(cardInLoop).asString());
            innerBox.getChildren().add(card);
        }

        ListView<Ticket> ticketListView = new ListView<>(observableGameState.getPlayerTickets());
        ticketListView.setId("tickets");

        HBox theBigBox = new HBox(ticketListView,innerBox);
        theBigBox.getStylesheets().addAll("decks.css","colors.css");

        return theBigBox;
    }

    /**
     * Create the cards view
     * @param observableGameState the state of the game for the player being observed
     * @param drawTicketsHandlerObjectProperty Object Property that handles the tickets drawn by player
     * @param drawCardHandlerObjectProperty Object Property that handles the cards drawn by player
     * @return creates the cards view
     */
    public static VBox createCardsView(ObservableGameState observableGameState,
                                       ObjectProperty<ActionHandler.DrawTicketsHandler> drawTicketsHandlerObjectProperty,
                                       ObjectProperty<ActionHandler.DrawCardHandler> drawCardHandlerObjectProperty){
        VBox faceUpCardVBox = new VBox();

        Button ticketsButton = sideButton(StringsFr.TICKETS,observableGameState.ticketAmountProperty());
        ticketsButton.getStyleClass().add("gauged");
        faceUpCardVBox.getChildren().add(ticketsButton);
        ticketsButton.disableProperty().bind(drawTicketsHandlerObjectProperty.isNull());
        ticketsButton.setOnMouseClicked(e -> drawTicketsHandlerObjectProperty.get().onDrawTickets());

        //face up cards
        for (int i : Constants.FACE_UP_CARD_SLOTS) {
            StackPane card = nodeForCard(null);
            observableGameState.faceUpCard(i).addListener((observableValue,old,newValue) ->{
                if (!newValue.name().equals(Card.LOCOMOTIVE.name())) {
                    card.getStyleClass().set(0,newValue.name());
                }
                else {
                    card.getStyleClass().set(0,"NEUTRAL");
                }
            });
            card.disableProperty().bind(drawCardHandlerObjectProperty.isNull());
            card.setOnMouseClicked(e -> drawCardHandlerObjectProperty.get().onDrawCard(i));

            faceUpCardVBox.getChildren().add(card);
        }

        Button cardButton = sideButton(StringsFr.CARDS,observableGameState.cardAmountProperty());
        cardButton.getStyleClass().add("gauged");
        cardButton.disableProperty().bind(drawCardHandlerObjectProperty.isNull());
        cardButton.setOnMouseClicked(e ->
        drawCardHandlerObjectProperty.get().onDrawCard(Constants.DECK_SLOT));
        faceUpCardVBox.getChildren().add(cardButton);

        faceUpCardVBox.getStylesheets().addAll("decks.css", "colors.css");
        faceUpCardVBox.setId("card-pane");
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
