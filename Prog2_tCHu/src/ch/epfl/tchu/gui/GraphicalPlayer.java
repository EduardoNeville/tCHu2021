package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import static ch.epfl.tchu.gui.ActionHandler.*;

import ch.epfl.tchu.net.ChatMessage;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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
    private final PlayerId id;
    private final ObservableList<Text> infoList = FXCollections.observableArrayList();
    private final ObservableList<ChatMessage> chatList = FXCollections.observableArrayList();
    private final StringProperty chatMessage = new SimpleStringProperty(null);
    private final ObjectProperty<DrawTicketsHandler> drawTicketH = new SimpleObjectProperty<>();
    private final ObjectProperty<DrawCardHandler> drawCardH = new SimpleObjectProperty<>();
    private final ObjectProperty<ClaimRouteHandler> claimRouteH = new SimpleObjectProperty<>();
    private final ObjectProperty<ChatHandler> chatHandlerH = new SimpleObjectProperty<>();
    private final ObjectProperty<TradeDealHandler> tradeDealH = new SimpleObjectProperty<>();


    /**
     * Constructs a graphical player with as it's associated player the one given as argument
     * and launches the game's UI.
     *
     * @param playerId this instance's player
     * @param names    the player names
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> names, ChatHandler chatHandler) {
        oGameState = new ObservableGameState(playerId);
        chatHandlerH.set(chatHandler);
        id = playerId;

        this.primaryStage = new Stage();
        primaryStage.setTitle("tCHu — " + names.get(playerId));
        primaryStage.setScene(new Scene(new BorderPane()));

        Node mapView = MapViewCreator
                .createMapView(oGameState, claimRouteH, (this::chooseClaimCards));
        Node cardsView = DecksViewCreator
                .createCardsView(oGameState, drawTicketH, drawCardH, () -> makeTradeDeal(tradeDealH.get()));
        Node handView = DecksViewCreator
                .createHandView(oGameState);

        Node infoView = InfoViewCreator
                .createInfoView(playerId, names, oGameState, infoList, chatList, chatMessage, chatHandlerH);


        BorderPane mainPane =
                new BorderPane(mapView, null, cardsView, handView, infoView);

        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }


    /**
     * Updates the state of the game with the new given game and public states
     *
     * @param newPublicGameState new public game state
     * @param newPlayerState     new player state
     */
    public void setState(PublicGameState newPublicGameState, PlayerState newPlayerState) {
        oGameState.setState(newPublicGameState, newPlayerState);
    }

    /**
     * Adds the given message to the info panel of the UI
     *
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
     *
     * @param drawTicketsHandler the handler that handles ticket drawing
     * @param drawCardHandler    the handler that handles card drawing
     * @param claimRouteHandler  the handler that handles route claiming
     */
    public void startTurn(DrawTicketsHandler drawTicketsHandler,
                          DrawCardHandler drawCardHandler,
                          ClaimRouteHandler claimRouteHandler,
                          TradeDealHandler tradeDealHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(drawTicketsHandler != null &&
                drawCardHandler != null && claimRouteHandler != null);
        if (oGameState.canDrawTickets())
            drawTicketH.set(() -> {
                emptyHandlers();
                drawTicketsHandler.onDrawTickets();
            });
        if (oGameState.canDrawCards())
            drawCardH.set((slot) -> {
                emptyHandlers();
                drawCardHandler.onDrawCard(slot);
            });

        tradeDealH.set(d -> {
            emptyHandlers();
            tradeDealHandler.onDealOffer(d);
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
        tradeDealH.set(null);
    }

    /**
     * Open a windows with the the different tickets choices the player can choose and uses the given
     * handler to receive said tickets.
     *
     * @param tickets              ticket choices
     * @param chooseTicketsHandler ticket handler
     */
    public void chooseTickets(SortedBag<Ticket> tickets, ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(tickets.size() == 3 || tickets.size() == 5);

        ListView<Ticket> listView = new ListView<>(FXCollections.observableList(tickets.toList()));
        ObservableList<Ticket> selectedItems = listView.getSelectionModel().getSelectedItems();
        Button confirmButton = new Button(StringsFr.CHOOSE);
        confirmButton.disableProperty().bind(Bindings.lessThan(Bindings.size(selectedItems), tickets.size() - 2));
        //  Bindings.size(selectedItems).
        //        lessThanOrEqualTo(selectedItems.size() - 2));

        Stage selectionWindow = choiceWindow(StringsFr.TICKETS_CHOICE,
                String.format(StringsFr.CHOOSE_TICKETS, tickets.size() - Constants.DISCARDABLE_TICKETS_COUNT,
                        StringsFr.plural(tickets.size() - Constants.DISCARDABLE_TICKETS_COUNT)),
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
     *
     * @param drawCardHandler action handler for drawing cards
     */
    public void drawCard(DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        drawCardH.set((slot) -> {
            emptyHandlers();
            drawCardHandler.onDrawCard(slot);
        });
    }

    /**
     * Open a windows with the the different initial card choices the player can choose to claim a route
     * and uses the given handler to claim said route.
     *
     * @param cardOptions        possible card combinations to claim
     * @param chooseCardsHandler action handler for cards
     */
    public void chooseClaimCards(List<SortedBag<Card>> cardOptions, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        if (cardOptions.size() == 1) {
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

        Stage selectionWindow = choiceWindow(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_CARDS,
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
     *
     * @param cardOptions        possible additional card combinations to claim
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


        Stage selectionWindow = choiceWindow(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_ADDITIONAL_CARDS,
                listView, confirmButton);

        confirmButton.setOnAction((e) -> {
                    selectionWindow.hide();
                    chooseCardsHandler.onChooseCards(selectedItems.get(0));
                }
        );

        selectionWindow.show();
    }

    public void makeTradeDeal(TradeDealHandler tradeDealHandler) {
        assert isFxApplicationThread();
        Stage choiceWindow = new Stage(StageStyle.UTILITY);
        choiceWindow.initOwner(primaryStage);
        choiceWindow.initModality(Modality.WINDOW_MODAL);
        choiceWindow.setOnCloseRequest(Event::consume);

        BooleanProperty canTradeCards = new SimpleBooleanProperty();
        ChoiceBox<Card> cards1G = new ChoiceBox<>();
        cards1G.getSelectionModel().selectFirst();
        cards1G.getItems().addAll(Card.ALL);
        cards1G.getItems().add(null);

        TextField card1Amount = new TextField("1");

        cards1G.valueProperty().addListener((o, oV, nV) -> {
            if (o.getValue() == null ||
                    oGameState.getPlayerCardsCount(o.getValue()).get() < Integer.parseInt(card1Amount.textProperty().get())) {
                canTradeCards.set(true);
            } else canTradeCards.set(false);
        });

        card1Amount.textProperty().addListener((o, oV, nV) -> {
            //regex for only 1-9 or nothing for easier writing
            if (!nV.matches("[1-9]|")) {
                card1Amount.textProperty().setValue(oV);
            } else if(!nV.isEmpty()) {
                canTradeCards.set(oGameState.getPlayerCardsCount(cards1G.getValue()).get() < Integer.parseInt(nV));
            } else canTradeCards.set(false); //if ""
        });
        card1Amount.disableProperty().bind(Bindings.isNull(cards1G.valueProperty()));


        ChoiceBox<Ticket> ticketG = new ChoiceBox<>();
        ticketG.getItems().add(null);
        ticketG.getItems().addAll(oGameState.getPlayerTickets());

        ChoiceBox<Route> routesG = new ChoiceBox<>();
        routesG.getItems().add(null);
        routesG.getItems().addAll(oGameState.getPlayerRoutes(id));


        ChoiceBox<Card> cards1R = new ChoiceBox<>();
        cards1R.getItems().add(null);
        cards1R.getItems().addAll(Card.ALL);

        TextField card2Amount = new TextField("1");
        card2Amount.textProperty().addListener((o, oV, nV) -> {
            if (!oV.matches("[1-9]|| "))
                card2Amount.textProperty().setValue(oV);


        });
        card2Amount.disableProperty().bind(Bindings.isNull(cards1R.valueProperty()));


        ChoiceBox<Ticket> ticketR = new ChoiceBox<>();
        SortedBag<Ticket> otherTickets = SortedBag.of(
                ChMap.tickets())
                .difference(
                        SortedBag.of(oGameState.getPlayerTickets()));
        ObservableList<Ticket> tickets = FXCollections.observableArrayList();
        tickets.addAll(otherTickets.toList());
        ticketR.getItems().add(null);
        ticketR.getItems().addAll(tickets);


        ChoiceBox<Route> routesR = new ChoiceBox<>();
        routesR.getItems().add(null);
        routesR.getItems().addAll(oGameState.getPlayerRoutes(id.next()));

        BooleanProperty correctDeal = new SimpleBooleanProperty();
        correctDeal.bind(Bindings.isNull(cards1G.valueProperty()).or(canTradeCards.not()));
        BooleanProperty allEmpty = new SimpleBooleanProperty();
        allEmpty.bind(Bindings.isNull(cards1G.valueProperty()).and(Bindings.isNull(cards1R.valueProperty()))
        .and(Bindings.isNull(ticketG.valueProperty())).and(Bindings.isNull(ticketR.valueProperty()))
        .and(Bindings.isNull(routesG.valueProperty())).and(Bindings.isNull(routesR.valueProperty())));

        Button confirmButton = new Button("SALKDJHASKDHSAK CONFIRMER");
        confirmButton.disableProperty().bind(correctDeal.not().or(allEmpty));
        confirmButton.setOnAction(a -> {
            choiceWindow.hide();
            SortedBag<Card> offered = cards1G.getValue() == null ? SortedBag.of()
                    : SortedBag.of(Integer.parseInt(card1Amount.textProperty().get()), cards1G.getValue());
            Route rG = routesG.getValue();
            Ticket tG = ticketG.getValue();
            Route rR = routesR.getValue();
            Ticket tR = ticketR.getValue();
            SortedBag<Card> cardsReceive = cards1R.getValue() == null ? SortedBag.of()
                    : SortedBag.of(Integer.parseInt(card2Amount.textProperty().get()), cards1R.getValue());
            tradeDealH.get().onDealOffer(new TradeDeal( rR, cardsReceive, tR, rG, offered, tG));
        });

        VBox VBox = new VBox(card1Amount, cards1G, ticketG, routesG, new Separator(), card2Amount, cards1R, ticketR, routesR, confirmButton);
        Scene chooserScene = new Scene(VBox);
        chooserScene.getStylesheets().add("chooser.css");

        choiceWindow.setTitle("Trade Deal");
        TextFlow textFlow = new TextFlow(new Text("SALKDASLDKASLKDJ"));

//        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

//        choiceBox.getChildren().addAll(textFlow, listView, button);


        choiceWindow.setScene(chooserScene);
        choiceWindow.show();

    }

    public void acceptTradeDeal(TradeDeal tradeDeal, BlockingQueue<Boolean> queue) {
        assert isFxApplicationThread();
        Stage choiceWindow = new Stage(StageStyle.UTILITY);
        choiceWindow.initOwner(primaryStage);
        choiceWindow.initModality(Modality.WINDOW_MODAL);
        choiceWindow.setOnCloseRequest(Event::consume);

        boolean hasCards = true;
        for (Card c :tradeDeal.cardsReceive()) {
            if(oGameState.getPlayerCardsCount(c).get() < tradeDeal.cardsReceive().countOf(c))
                hasCards = false;
        }
        boolean hasTicket = false;
        if (tradeDeal.ticketsReceive() != null){
            for (Ticket t :
                    oGameState.getPlayerTickets()) {
                if(tradeDeal.ticketsReceive().compareTo(t) == 0)  hasTicket = true;
            }
        }

        System.out.println(hasCards + " " + hasTicket);

        Text dealText = new Text(StringsFr.trade(tradeDeal));
        Button acceptButton = new Button("SALKDJHASKDHSAK accepter");
        Button declineButton = new Button("SALKDJHASKDHSAK refuser");


        acceptButton.setOnAction(a -> {
            try {
                choiceWindow.hide();
                queue.put(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        acceptButton.disableProperty().set(!hasCards || !hasTicket);

        declineButton.setOnAction(a -> {
            try {
                choiceWindow.hide();
                queue.put(false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        VBox VBox = new VBox(dealText, acceptButton, declineButton);
        Scene chooserScene = new Scene(VBox);
        chooserScene.getStylesheets().add("chooser.css");

        choiceWindow.setTitle("Trade Deal");
        TextFlow textFlow = new TextFlow(new Text("SALKDASLDKASLKDJ"));

//        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

//        choiceBox.getChildren().addAll(textFlow, listView, button);


        choiceWindow.setScene(chooserScene);
        choiceWindow.show();
    }

    private <T> Stage choiceWindow(String string, String string2,
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

    public void receiveMessage(ChatMessage message) {
        chatList.add(message);
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