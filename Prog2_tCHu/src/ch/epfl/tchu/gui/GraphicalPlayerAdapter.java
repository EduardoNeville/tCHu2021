package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.ChatMessage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import static javafx.application.Platform.runLater;

/**
 * GraphicalPlayerAdapter class
 *
 * @author Eduardo Neville (314677)
 */

public class GraphicalPlayerAdapter implements Player, ChatUser{

    private GraphicalPlayer graphicalPlayer;

    private final ArrayBlockingQueue<SortedBag<Ticket>> sortedBagsTickets = new ArrayBlockingQueue<>(1);
    private final ArrayBlockingQueue<SortedBag<Card>> sortedBagsCards = new ArrayBlockingQueue<>(1);
    private final ArrayBlockingQueue<TurnKind> turnKinds = new ArrayBlockingQueue<>(1);
    private final ArrayBlockingQueue<Route> routes = new ArrayBlockingQueue<>(1);
    private final ArrayBlockingQueue<Integer> deckSlot = new ArrayBlockingQueue<>(1);
    private final ArrayBlockingQueue<String> messagesOutgoing = new ArrayBlockingQueue<>(1);
    private ActionHandler.ChatHandler chatHandler;

    /**
     * initPlayers method being overridden from Player
     * @param ownId
     *          id of the player
     * @param playerNames Names of initial players
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
//        this.chatHandler = e -> sendChatMessage();
        runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames, chatHandler));
    }

    /**
     * setInitialTicketChoice method being overridden from Player
     * @param tickets Player tickets
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.
                chooseTickets(tickets,sortedBagsTickets::add));
    }

    /**
     * receiveInfo method being overridden from Player
     * @param info Information from
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**+
     * updateState method being overridden from Player
     * @param newState
     *          new public game state
     * @param ownState new playerState
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    /**
     * chooseInitialTickets method being overridden from Player
     * @return SortedBag of Player Tickets
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return blockingQ(sortedBagsTickets);
    }

    /**
     * nextTurn method being overridden from Player
     * @return the next Turn kind?
     */
    @Override
    public TurnKind nextTurn(){
        runLater(() -> graphicalPlayer.startTurn(
                () -> turnKinds.add(TurnKind.DRAW_TICKETS),
                (index) -> {
                    turnKinds.add(TurnKind.DRAW_CARDS);
                    deckSlot.add(index); },
                (route, cardDeck) ->{
                    turnKinds.add(TurnKind.CLAIM_ROUTE);
                    routes.add(route);
                    sortedBagsCards.add(cardDeck);}));
        return blockingQ(turnKinds);
    }

    /**
     * chooseTickets method being overridden from Player
     * @param ts tickets the player chooses
     * @return A sortedBag of the chosen Tickets
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> ts) {
        runLater(() -> graphicalPlayer.chooseTickets(ts,
                sortedBagsTickets::add));
        return blockingQ(sortedBagsTickets);
    }

    /**
     * drawSlot method being overridden from Player
     * @return the deck slot
     */
    @Override
    public int drawSlot() {
        if (deckSlot.isEmpty()){
            runLater(() -> graphicalPlayer.drawCard(
                    deckSlot::add));
        }
        return blockingQ(deckSlot);
    }

    /**
     * claimedRoute method being overridden from Player
     * @return The route claimed by player
     */
    @Override
    public Route claimedRoute() {
        return routes.remove();
    }

    /**
     * initialClaimCards method being overridden from Player
     * @return the cards claimed at the beginning
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        return sortedBagsCards.remove();
    }

    /**
     * chooseAdditionalCards method being overridden from Player
     * @param options
     *          list of options the player can choose from to finalise claim
     * @return a sortedbag of the chosen cards
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options,
                sortedBagsCards::add));
        return blockingQ(sortedBagsCards);
    }

    @Override
    public void receiveChatMessage(ChatMessage message) {
        runLater( () -> graphicalPlayer.receiveMessage(message));
    }

    @Override
    public void receiveChatMessageHandler(ActionHandler.ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }


    private <T> T blockingQ(ArrayBlockingQueue<T> blockingQueue){
        try {
            return blockingQueue.take();
        } catch (InterruptedException e) {
            throw new IllegalArgumentException();
        }
    }
}