package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;


class DecksViewCreator {

    private StackPane nodeForCard(Card card){
        Rectangle outside = new Rectangle(60,90);


        Rectangle inside = new Rectangle(40,70);
        inside.getStyleClass().add("inside");

        Rectangle image = new Rectangle(40,70);
        image.getStyleClass().add("train-image");

        StackPane cardColour = new StackPane(outside, inside);
        cardColour.getStyleClass().addAll(card.name(),"card");

        return cardColour;
    }

    public HBox createHandView(ObservableGameState observableGameState) {
        
        HBox innerBox = new HBox();
        innerBox.setId("hand-pane");

        for (Card cardInLoop:Card.ALL) {
            ReadOnlyIntegerProperty count = observableGameState.cardTypeCount(cardInLoop);
            StackPane card = nodeForCard(cardInLoop);
            card.visibleProperty().bind(Bindings.greaterThan(count, 0));
            Text counter = new Text();
            counter.getStyleClass().add("count");
            //TODO verify that asString works if not use
            //Bindings.convert(observableGameState.cardTypeCount(cardInLoop))
            counter.textProperty().bind(observableGameState.cardTypeCount(cardInLoop).asString());
        //TODO addListener
            count.addListener();
        }

        ListView<Ticket> ticketListView = new ListView<>();
        ticketListView.setId("tickets");

        HBox theBigBox = new HBox(ticketListView,innerBox);
        theBigBox.getStylesheets().addAll("decks.css","color.css");

        return theBigBox;
    }

    //todo change attributes right?
    // prev atributes: PlayerState playerState, CardState cardState, CardState ticketState
    public void createCardsView(ObservableGameState observableGameState) {
        Button cardButton = new Button();
        cardButton.getStyleClass().add("gauged");

        Button ticketsButton = new Button();
        ticketsButton.getStyleClass().add("gauged");

        for (Route route:ChMap.routes()) {
            Rectangle gaugeForeground = new Rectangle();

            //todo is this correct?
            gaugeForeground.getStyleClass().add(cardButton.getStyle());

            ReadOnlyIntegerProperty pctProperty = observableGameState.cardPercentage;
            gaugeForeground.widthProperty().bind(pctProperty.multiply(50).divide(100));
        }

        VBox bigVBox = new VBox();
        bigVBox.getStylesheets().addAll("decks.css", "color.css");
    }

}
