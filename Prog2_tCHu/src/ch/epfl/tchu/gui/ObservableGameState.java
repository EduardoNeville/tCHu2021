package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import static ch.epfl.tchu.game.Constants.*;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

/**
 * Class that represents an observable game state of tchu that is specific to one player.
 *
 * @author Martin Sanchez Lopez (313238)
 */
public final class ObservableGameState {
    private final PlayerId player;
    private PublicGameState gameState = null;
    private PlayerState currentPlayerState = null;

    private final IntegerProperty ticketAmount = new SimpleIntegerProperty(0);
    private final IntegerProperty cardAmount = new SimpleIntegerProperty(0);
    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<Route,ObjectProperty<PlayerId>> routeOwners = new HashMap<>(); //or simple array?

    private final Map<PlayerId, IntegerProperty> playerTicketCount;// = new EnumMap(PlayerId.class);
    private final Map<PlayerId, IntegerProperty> playerCardCount;// = new EnumMap(PlayerId.class);
    private final Map<PlayerId, IntegerProperty> playerCarCount;// = new EnumMap(PlayerId.class);
    private final Map<PlayerId, IntegerProperty> playerConstructionPoints;// = new EnumMap(PlayerId.class);

    private final ObservableList<Ticket> playerTickets = FXCollections.observableArrayList();

    private final Map<Card, IntegerProperty> playerCardsCount = new EnumMap<>(Card.class);
    private final Map<Route,BooleanProperty> playerClaimableRoutes = new HashMap<>();

    /**
     * Creates an observable game state with the given player as the associated player to this instance.
     *
     * @param playerId the player associated with this instance
     */
    public ObservableGameState(PlayerId playerId) {
        player = Objects.requireNonNull(playerId);

        faceUpCards = createFaceUpCards();

        playerTicketCount = initMap();
        playerCardCount = initMap();
        playerCarCount = initMap();
        playerConstructionPoints = initMap();

        for (Card c : Card.ALL) {
            playerCardsCount.put(c, new SimpleIntegerProperty(0));
        }
        for (Route route: ChMap.routes()) {
            playerClaimableRoutes.put(route,new SimpleBooleanProperty());
            routeOwners.put(route, new SimpleObjectProperty<>());
        }
    }

    /**
     * Makes and returns a map of playerdIds with instantiated integer properties to 0.
     *
     * @return and returns a map of playerdIds with instantiated integer properties to 0
     */
    private Map<PlayerId, IntegerProperty> initMap() {
        Map<PlayerId, IntegerProperty> map = new EnumMap<>(PlayerId.class);
        for (PlayerId id : PlayerId.ALL) {
            map.put(id, new SimpleIntegerProperty(0));
        }
        return map;
    }

    /**
     * Updates the states and properties of this instance's public game and player states to the new given ones.
     *
     * @param newPublicGameState new public game state
     * @param newPlayerState     new player state
     */
    public void setState(PublicGameState newPublicGameState, PlayerState newPlayerState) {
        gameState = newPublicGameState;
        currentPlayerState = newPlayerState;

        ticketAmount.set((gameState.ticketsCount() * 100) / ChMap.tickets().size());
        cardAmount.set((gameState.cardState().deckSize() * 100) / TOTAL_CARDS_COUNT);

        for (int slot : FACE_UP_CARD_SLOTS) {
            Card newCard = gameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }
        for (PlayerId id : PlayerId.ALL) {
            PublicPlayerState publicPlayerState = gameState.playerState(id);
            playerTicketCount.get(id).setValue(publicPlayerState.ticketCount());
            playerCardCount.get(id).setValue(publicPlayerState.cardCount());
            playerCarCount.get(id).setValue(publicPlayerState.carCount());
            playerConstructionPoints.get(id).setValue(publicPlayerState.claimPoints());
            publicPlayerState.routes().forEach(route -> routeOwners.get(route).set(id));
        }

        currentPlayerState.tickets().forEach(t -> {
            if (!playerTickets.contains(t)) {
                playerTickets.add(t);
            } else if(!currentPlayerState.tickets().contains(t)){
                playerTickets.remove(t);
            }
        });
        Card.ALL.forEach(c -> playerCardsCount.get(c).set(currentPlayerState.cards().countOf(c)));

        for (Route route : ChMap.routes()) {
            playerClaimableRoutes.get(route).set(
                    //boolean : three conditions for the route to be claimable by the player
                    gameState.currentPlayerId() == player
                            &&routeOwners.get(route).getValue() == null
                            && currentPlayerState.canClaimRoute(route));
        }
    }

    /**
     * Returns a read only property of the amount of tickets left (in %).
     *
     * @return a read only property of the amount of tickets left (in %)
     */
    public ReadOnlyIntegerProperty ticketAmountProperty() {
        return ticketAmount;
    }

    /**
     * Returns a read only property of the amount of card left in the deck (in %).
     *
     * @return a read only property of the amount of card left in the deck (in %)
     */
    public ReadOnlyIntegerProperty cardAmountProperty() {
        return cardAmount;
    }

    /**
     * Returns a read only property of the card at the given slot.
     *
     * @param slot slot of the card
     * @return a read only property of the card at the given slot
     * @throws IndexOutOfBoundsException if the slot number is not between 0 (included) and the number of
     *                                   face up cards (excluded)
     */
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        Objects.checkIndex(slot, FACE_UP_CARDS_COUNT);
        return faceUpCards.get(slot);
    }

    /**
     * Returns a read only property of the owner of a route.
     * @param route Route
     * @return a read only property of the owner of a route
     */
    public ReadOnlyObjectProperty<PlayerId> getRouteOwners(Route route) {
        return routeOwners.get(route);
    }

    public ObservableList<Route> getPlayerRoutes(PlayerId id){
        ObservableList<Route> list = FXCollections.observableArrayList();
        routeOwners.keySet().forEach( r -> {
            if(routeOwners.get(r).getValue() != null &
                    routeOwners.get(r).getValue() == id){
                System.out.println(routeOwners.get(r).getValue());
                list.add(r);
            }
        });
        return FXCollections.unmodifiableObservableList(list);
    }

    /**
     * Returns a read only property of the ticket count of the given player.
     * @param playerId player id of the player
     * @return a read only property of the ticket count of the given player
     */
    public ReadOnlyIntegerProperty getPlayerTicketCount(PlayerId playerId) {
        return playerTicketCount.get(playerId);
    }

    /**
     * Returns a read only property of the card count of the given player.
     * @param playerId player id of the player
     * @return a read only property of the card count of the given player
     */
    public ReadOnlyIntegerProperty getPlayerCardCount(PlayerId playerId) {
        return playerCardCount.get(playerId);
    }

    /**
     * Returns a read only property of the car count of the given player.
     * @param playerId player id of the player
     * @return a read only property of the car count of the given player
     */
    public ReadOnlyIntegerProperty getPlayerCarCount(PlayerId playerId) {
        return playerCarCount.get(playerId);
    }

    /**
     * Returns a read only property of the construction points of the given player.
     * @param playerId player id of the player
     * @return a read only property of the construction points of the given player
     */
    public ReadOnlyIntegerProperty getPlayerConstructionPoints(PlayerId playerId) {
        return playerConstructionPoints.get(playerId);
    }

    /**
     * Returns an observable list of the tickets of the associated player.
     * @return  an observable list of the tickets of the associated player
     */
    public ObservableList<Ticket> getPlayerTickets() {
        return FXCollections.unmodifiableObservableList(playerTickets);
    }

    /**
     * Returns a read only property of the card count of the given card of the associated player.
     * @param card Card whose count is returned
     * @return a read only property of the card count of the given card of the associated player
     */
    public ReadOnlyIntegerProperty getPlayerCardsCount(Card card) {
        if(card == null) return new SimpleIntegerProperty(0);
        return playerCardsCount.get(card);
    }

    /**
     * Returns a read only property of whether the associated player can claim the given route.
     * @param route Route to check
     * @return a read only property of whether the associated player can claim the given route
     */
    public ReadOnlyBooleanProperty getPlayerClaimableRoute(Route route) {
        return playerClaimableRoutes.get(route);
    }

    /**
     * Returns an instantiated list of observable cards.
     * @return an instantiated list of observable cards
     */
    private List<ObjectProperty<Card>> createFaceUpCards() {
        List<ObjectProperty<Card>> list = new ArrayList<>();
        for (int i = 0; i < FACE_UP_CARDS_COUNT; i++) {
            list.add(new SimpleObjectProperty<>());
        }
        return list;
    }

    /**
     * Returns whether a ticket can be drawn.
     * @return true if a ticket can be drawn, false otherwise
     */
    public boolean canDrawTickets() {
        return gameState.canDrawTickets();
    }

    /**
     * Returns whether cards can be drawn.
     * @return true if cards can be drawn, false otherwise
     */
    public boolean canDrawCards() {
        return gameState.canDrawCards();
    }

    /**
     * Returns a bag of possible claim combinations for the given route.
     *
     * @param route route to claim
     * @return a bag of possible claim combinations for the given route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return currentPlayerState.possibleClaimCards(route);
    }
}