package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static ch.epfl.tchu.gui.ActionHandler.*;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static javafx.application.Platform.isFxApplicationThread;

public class GraphicalPlayer {
    private final Stage primaryStage;

    private final ObservableGameState oGameState;
    private final PlayerId playerId;
    private final Map<PlayerId, String> names;
    private final ObservableList<Text> infoList = FXCollections.observableArrayList();
    private final ObjectProperty<DrawTicketsHandler> drawTicketH = new SimpleObjectProperty<>();
    private final ObjectProperty<DrawCardHandler> drawCardH = new SimpleObjectProperty<>();
    private final ObjectProperty<ClaimRouteHandler> claimRouteH = new SimpleObjectProperty<>();


    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> names) {
        oGameState = new ObservableGameState(playerId);
        this.playerId = playerId;
        this.names = names;

        this.primaryStage = new Stage();
        primaryStage.setTitle("tCHu — " + names.get(playerId));
        primaryStage.setScene(new Scene(new BorderPane()));

        Node mapView = MapViewCreator
                .createMapView(oGameState, claimRouteH, ((options, handler) -> chooseClaimCards(options, handler)));
        Node cardsView = DecksViewCreator
                .createCardsView(oGameState, drawTicketH, drawCardH);
        Node handView = DecksViewCreator
                .createHandView(oGameState);

        ObservableList<Text> infos = FXCollections.observableArrayList(
                new Text("Première information.\n"),
                new Text("\nSeconde information.\n"));
        Node infoView = InfoViewCreator
                .createInfoView(playerId, names, oGameState, infoList);


        BorderPane mainPane =
                new BorderPane(mapView, null, cardsView, handView, infoView);

//        BorderPane mainPane =
//                new BorderPane(mapView, null, null, null, null);

//                new BorderPane(mapView, null, cardsView, handView, null);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();

        PlayerState p1State =
                new PlayerState(SortedBag.of(ChMap.tickets().subList(0, 3)),
                        SortedBag.of(1, Card.WHITE, 3, Card.RED),
                        ChMap.routes().subList(0, 3));

//        setState(oGameState, p1State);
    }


    public void setState(PublicGameState newPublicGameState, PlayerState newPlayerState) {
        oGameState.setState(newPublicGameState, newPlayerState);
    }

    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        if (infoList.size() < 5)
            infoList.add(new Text(message + "\n"));
        else {
            infoList.add(0, new Text(message + "\n"));
            infoList.remove(5);
        }
    }


    public void startTurn(DrawTicketsHandler drawTicketsHandler,
                          DrawCardHandler drawCardHandler,
                          ClaimRouteHandler claimRouteHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(drawTicketsHandler != null &&
                drawCardHandler != null && claimRouteHandler != null);
        if (oGameState.canDrawTickets())
            drawTicketH.set(new DrawTicketsHandler() {
                @Override
                public void onDrawTickets() {
                    emptyHandlers();
                    drawTicketsHandler.onDrawTickets();
                }
            });
        if (oGameState.canDrawCards())
            drawCardH.set(new DrawCardHandler() {
                @Override
                public void onDrawCard(int slot) {
                    emptyHandlers();
                    drawCardHandler.onDrawCard(slot);
                }
            });

        claimRouteH.set(new ClaimRouteHandler() {
            @Override
            public void onClaimRoute(Route route, SortedBag<Card> cardDeck) {
                emptyHandlers();
                claimRouteHandler.onClaimRoute(route, cardDeck);
            }
        });
    }

    private void emptyHandlers() {
        drawTicketH.set(null);
        drawCardH.set(null);
        claimRouteH.set(null);
    }

    public void chooseTickets(SortedBag<Ticket> tickets, ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(tickets.size() == 3 || tickets.size() == 5);

        ListView listView = new ListView();
        ObservableList selectedItems = listView.getSelectionModel().getSelectedItems();
        Button confirmButton = new Button();
        confirmButton.disableProperty().bind(
                Bindings.size(selectedItems).
                        lessThanOrEqualTo(selectedItems.size() - 2));

        Stage selectionWindow = choiceWindow(StringsFr.TICKETS_CHOICE,
                listView, confirmButton);

        confirmButton.setOnAction((e) -> {
                    selectionWindow.hide();
                    chooseTicketsHandler.onChooseTickets(SortedBag.of(selectedItems));
                }
        );

        selectionWindow.show();

    }

    public void drawCard(DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
    }

    public void chooseClaimCards(List<SortedBag<Card>> cardOptions, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        if(cardOptions.size() == 1) {
            chooseCardsHandler.onChooseCards(cardOptions.get(0));
            return;
        }

        ListView listView = new ListView();
        ObservableList selectedItems = listView.getSelectionModel().getSelectedItems();
        Button confirmButton = new Button();
        confirmButton.disableProperty().bind(
                Bindings.size(selectedItems).isNotEqualTo(1));

        Stage selectionWindow = choiceWindow(StringsFr.TICKETS_CHOICE,
                listView, confirmButton);

        confirmButton.setOnAction((e) -> {
                    selectionWindow.hide();
                    chooseCardsHandler.onChooseCards(SortedBag.of(selectedItems));
                }
        );

        selectionWindow.show();
    }

    public void chooseAdditionalCards(List<SortedBag<Card>> cardOptions, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(!cardOptions.isEmpty());

        ListView listView = new ListView();
        ObservableList selectedItems = listView.getSelectionModel().getSelectedItems();
        Button confirmButton = new Button();


        Stage selectionWindow = choiceWindow(StringsFr.TICKETS_CHOICE,
                listView, confirmButton);

        confirmButton.setOnAction((e) -> {
                    selectionWindow.hide();
                    chooseCardsHandler.onChooseCards(SortedBag.of(selectedItems));
                }
        );

        selectionWindow.show();
    }


    private Stage choiceWindow(String string,
                                   ListView listView,
                                   Button button) {
        Stage choiceWindow = new Stage(StageStyle.UTILITY);
        choiceWindow.initOwner(primaryStage);
        choiceWindow.initModality(Modality.WINDOW_MODAL);
        choiceWindow.setOnCloseRequest((e) -> e.consume());
//        Preconditions.checkArgument(list instanceof List<Ticket>);


        VBox choiceBox = new VBox();

        Scene chooserScene = new Scene(choiceBox);
        chooserScene.getStylesheets().add("chooser.css");


        TextFlow textFlow = new TextFlow(new Text(string));

//        ListView<E> listView = new ListView();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        choiceBox.getChildren().addAll(textFlow, listView, button);

        choiceWindow.setScene(chooserScene);
        return choiceWindow;
    }


    private class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

        @Override
        public String toString(SortedBag<Card> cards) {
            return null;
        }

        @Override
        public SortedBag<Card> fromString(String s) {
            throw new UnsupportedOperationException();
        }
    }

}
