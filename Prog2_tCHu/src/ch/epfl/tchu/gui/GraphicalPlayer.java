package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import static ch.epfl.tchu.gui.ActionHandler.*;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.gui.StringsFr.AND_SEPARATOR;
import static javafx.application.Platform.isFxApplicationThread;

/**
 * Class that serves as the graphical interface for the player.
 *
 * @author Martin Sanchez Lopez (313238)
 */
public class GraphicalPlayer {
    private final Stage primaryStage;

    private final ObservableGameState oGameState;
    private final ObservableList<Text> infoList = FXCollections.observableArrayList();
    private final ObjectProperty<DrawTicketsHandler> drawTicketH = new SimpleObjectProperty<>();
    private final ObjectProperty<DrawCardHandler> drawCardH = new SimpleObjectProperty<>();
    private final ObjectProperty<ClaimRouteHandler> claimRouteH = new SimpleObjectProperty<>();


    /**
     * Constructs a graphical player with as it's associated player the one given as argument
     * and launches the game's UI.
     * @param playerId this instance's player
     * @param names the player names
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> names) {
        oGameState = new ObservableGameState(playerId);

        this.primaryStage = new Stage();
        primaryStage.setTitle("tCHu — " + names.get(playerId));
        primaryStage.setScene(new Scene(new BorderPane()));

        Node mapView = MapViewCreator
                .createMapView(oGameState, claimRouteH, (this::chooseClaimCards));
        Node cardsView = DecksViewCreator
                .createCardsView(oGameState, drawTicketH, drawCardH);
        Node handView = DecksViewCreator
                .createHandView(oGameState);

        Node infoView = InfoViewCreator
                .createInfoView(playerId, names, oGameState, infoList);


        BorderPane mainPane =
                new BorderPane(mapView, null, cardsView, handView, infoView);

        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }


    /**
     * Updates the state of the game with the new given game and public states
     * @param newPublicGameState new public game state
     * @param newPlayerState new player state
     */
    public void setState(PublicGameState newPublicGameState, PlayerState newPlayerState) {
        oGameState.setState(newPublicGameState, newPlayerState);
    }

    /**
     * Adds the given message to the info panel of the UI
     * @param message message to be communicated
     */
    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        if (infoList.size() < 5)
            infoList.add(new Text(message));
        else {
            infoList.add(new Text(message));
            infoList.remove(0);
        }
    }


    /**
     * Assigns the action handlers of the actions a player can do during his turn
     * @param drawTicketsHandler the handler that handles ticket drawing
     * @param drawCardHandler the handler that handles card drawing
     * @param claimRouteHandler the handler that handles route claiming
     */
    public void startTurn(DrawTicketsHandler drawTicketsHandler,
                          DrawCardHandler drawCardHandler,
                          ClaimRouteHandler claimRouteHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(drawTicketsHandler != null &&
                drawCardHandler != null && claimRouteHandler != null);
        if (oGameState.canDrawTickets())
            drawTicketH.set( () -> {
                emptyHandlers();
                drawTicketsHandler.onDrawTickets();
            });
        if (oGameState.canDrawCards())
            drawCardH.set( (slot) -> {
                emptyHandlers();
                drawCardHandler.onDrawCard(slot);
            });

        claimRouteH.set(((route, cardDeck) -> {
            emptyHandlers();
            claimRouteHandler.onClaimRoute(route, cardDeck);
        }));
    }

    /**
     * Empties handlers
     */
    private void emptyHandlers() {
        drawTicketH.set(null);
        drawCardH.set(null);
        claimRouteH.set(null);
    }

    /**
     * Open a windows with the the different tickets choices the player can choose and uses the given
     * handler to receive said tickets.
     * @param tickets ticket choices
     * @param chooseTicketsHandler ticket handler
     */
    public void chooseTickets(SortedBag<Ticket> tickets, ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(tickets.size() == 3 || tickets.size() == 5);

        ListView<Ticket> listView = new ListView<>(FXCollections.observableList(tickets.toList()));
        ObservableList<Ticket> selectedItems = listView.getSelectionModel().getSelectedItems();
        Button confirmButton = new Button(StringsFr.CHOOSE);
        confirmButton.disableProperty().bind(Bindings.lessThan(Bindings.size(selectedItems),tickets.size() -2));
        //  Bindings.size(selectedItems).
        //        lessThanOrEqualTo(selectedItems.size() - 2));

        Stage selectionWindow = choiceWindow(StringsFr.TICKETS_CHOICE,
                String.format(StringsFr.CHOOSE_TICKETS,tickets.size()-Constants.DISCARDABLE_TICKETS_COUNT,
                        StringsFr.plural(tickets.size()-Constants.DISCARDABLE_TICKETS_COUNT)),
                listView, confirmButton);

        confirmButton.setOnAction((e) -> {
                    selectionWindow.hide();
                    chooseTicketsHandler.onChooseTickets(SortedBag.of(selectedItems));
                }
        );
        selectionWindow.show();
    }

    /**
     * Lets the player draw one of the face up cards or a card from the deck.
     * @param drawCardHandler action handler for drawing cards
     */
    public void drawCard(DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        drawCardH.set( (slot) -> {
            emptyHandlers();
            drawCardHandler.onDrawCard(slot);
        });
    }

    /**
     * Open a windows with the the different initial card choices the player can choose to claim a route
     * and uses the given handler to claim said route.
     * @param cardOptions possible card combinations to claim
     * @param chooseCardsHandler action handler for cards
     */
    public void chooseClaimCards(List<SortedBag<Card>> cardOptions, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        if(cardOptions.size() == 1) {
            chooseCardsHandler.onChooseCards(cardOptions.get(0));
            return;
        }

        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(cardOptions));
        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));
        ObservableList<SortedBag<Card>> selectedItems = listView.getSelectionModel().getSelectedItems();
        Button confirmButton = new Button(StringsFr.CHOOSE);
        confirmButton.disableProperty().bind(
                Bindings.size(selectedItems).isNotEqualTo(1));

        Stage selectionWindow = choiceWindow(StringsFr.CARDS_CHOICE,StringsFr.CHOOSE_CARDS,
                listView, confirmButton);

        confirmButton.setOnAction((e) -> {
                    selectionWindow.hide();
                    chooseCardsHandler.onChooseCards(selectedItems.get(0));
                }
        );

        selectionWindow.show();
    }

    /**
     * Open a windows with the the different additional
     * card choices the player can choose to finalize a claim on a  route
     * and uses the given handler to claim said route.
     * @param cardOptions possible additional card combinations to claim
     * @param chooseCardsHandler action handler for cards
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> cardOptions, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(!cardOptions.isEmpty());

        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(cardOptions));
        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));
        ObservableList<SortedBag<Card>> selectedItems = listView.getSelectionModel().getSelectedItems();
        Button confirmButton = new Button(StringsFr.CHOOSE);


        Stage selectionWindow = choiceWindow(StringsFr.CARDS_CHOICE,StringsFr.CHOOSE_ADDITIONAL_CARDS,
                listView, confirmButton);

        confirmButton.setOnAction((e) -> {
                    selectionWindow.hide();
                    chooseCardsHandler.onChooseCards(selectedItems.get(0));
                }
        );

        selectionWindow.show();
    }

    private <T> Stage choiceWindow(String string,String string2,
                                   ListView<T> listView,
                                   Button button) {
        Stage choiceWindow = new Stage(StageStyle.UTILITY);
        choiceWindow.initOwner(primaryStage);
        choiceWindow.initModality(Modality.WINDOW_MODAL);
        choiceWindow.setOnCloseRequest(Event::consume);


        VBox choiceBox = new VBox();

        Scene chooserScene = new Scene(choiceBox);
        chooserScene.getStylesheets().add("chooser.css");

        choiceWindow.setTitle(string);
        TextFlow textFlow = new TextFlow(new Text(string2));

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        choiceBox.getChildren().addAll(textFlow, listView, button);

        choiceWindow.setScene(chooserScene);
        return choiceWindow;
    }

    /**
     * Redefines the toString method of sorted bags of cards
     */
    private static class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

        @Override
        public String toString(SortedBag<Card> cards) {
            StringBuilder cardStringBuilder = new StringBuilder();
            ArrayList<String> cardsArray = new ArrayList<>();

            for (Card c : cards.toSet()) {
                int cCount = cards.countOf(c);
                cardsArray.add(cCount + " " + Info.cardName(c, cCount));
            }
            if (cardsArray.size() > 1) {
                cardStringBuilder.append(String.join(", ", cardsArray.subList(0, cardsArray.size() - 1)));
                cardStringBuilder.append(AND_SEPARATOR);
            }

            return cardStringBuilder.append(cardsArray.get(cardsArray.size() - 1)).toString();
        }

        @Override
        public SortedBag<Card> fromString(String s) {
            throw new UnsupportedOperationException();
        }
    }

}