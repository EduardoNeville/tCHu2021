package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import static javafx.application.Platform.runLater;

/**
 * GraphicalPlayerAdapter class
 *
 * @author Eduardo Neville (314677)
 */

public class GraphicalPlayerAdapter implements Player{

    private GraphicalPlayer graphicalPlayer;

    private final ArrayBlockingQueue<List<SortedBag<Card>>> sortedBagListCards = new ArrayBlockingQueue<>(1);
    private final ArrayBlockingQueue<SortedBag<Ticket>> sortedBagTickets = new ArrayBlockingQueue<>(1);
    private final ArrayBlockingQueue<SortedBag<Card>> sortedBagCards = new ArrayBlockingQueue<>(1);
    private final ArrayBlockingQueue<TurnKind> turnKinds = new ArrayBlockingQueue<>(1);
    private final ArrayBlockingQueue<Integer> drawCardIndex = new ArrayBlockingQueue<>(1);
    private final ArrayBlockingQueue<Route> routes = new ArrayBlockingQueue<>(1);
    private final ArrayBlockingQueue<Integer> deckSlot = new ArrayBlockingQueue<>(1);

    public GraphicalPlayerAdapter(){

    }


    /**
     * initPlayers method being overridden from Player
     * @param ownId
     *          id of the player
     * @param playerNames Names of initial players
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
    }

    /**
     * setInitialTicketChoice method being overridden from Player
     * @param tickets Player tickets
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.
                    chooseTickets(tickets,
                            sortedBagTickets::add));
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
        return blockingQ(sortedBagTickets);
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
                                            drawCardIndex.add(index);
                                            },
                (route, bag) -> {
                                            turnKinds.add(TurnKind.CLAIM_ROUTE);
                                            Preconditions.checkArgument(routes.isEmpty()
                                                    && sortedBagCards.isEmpty());
                                            routes.add(route);
                                            sortedBagCards.add(bag);
                })
        );
        return blockingQ(turnKinds);
    }

    /**
     * chooseTickets method being overridden from Player
     * @param ts
     * @return A sortedBag of the chosen Tickets
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> ts) {
        setInitialTicketChoice(ts);
        return chooseInitialTickets();
    }

    /**
     * drawSlot method being overridden from Player
     * @return deckSlot
     */
    @Override
    public int drawSlot() {
        return blockingQ(deckSlot);
    }

    /**
     * claimedRoute method being overridden from Player
     * @return The route claimed by player
     */
    @Override
    public Route claimedRoute() {
        return blockingQ(routes);
    }

    /**
     * initialClaimCards method being overridden from Player
     * @return the cards claimed at the beginning
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        return blockingQ(sortedBagCards);
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
                (cardSortedBag) -> {
                    sortedBagCards.add(SortedBag.of(Card.ALL));
                    blockingQ(sortedBagCards);
                }));
        return blockingQ(sortedBagCards);
    }

    private <T> T blockingQ(ArrayBlockingQueue<T> blockingQueue){
        try {
            return blockingQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }
}
