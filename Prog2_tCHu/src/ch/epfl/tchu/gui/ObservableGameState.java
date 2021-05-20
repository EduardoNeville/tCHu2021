package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import static ch.epfl.tchu.game.Constants.*;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public final class ObservableGameState {
    private final PlayerId player;
    private PublicGameState gameState = null;
    private PlayerState currentPlayerState = null;

    private final IntegerProperty ticketAmount = new SimpleIntegerProperty(0);
    private final IntegerProperty cardAmount = new SimpleIntegerProperty(0);
    private final List<ObjectProperty<Card>> faceUpCards;
    private final List<ObjectProperty<PlayerId>> routeOwners
            = new ArrayList<>(Collections.nCopies(ChMap.routes().size(), new SimpleObjectProperty<>(null))); //or simple array?

    private final Map<PlayerId, IntegerProperty> playerTicketCount;// = new EnumMap(PlayerId.class);
    private final Map<PlayerId, IntegerProperty> playerCardCount;// = new EnumMap(PlayerId.class);
    private final Map<PlayerId, IntegerProperty> playerCarCount;// = new EnumMap(PlayerId.class);
    private final Map<PlayerId, IntegerProperty> playerConstructionPoints;// = new EnumMap(PlayerId.class);

    private final ObservableList<Ticket> playerTickets = FXCollections.observableArrayList();
    private final Map<Card, IntegerProperty> playerCardsCount = new EnumMap<>(Card.class);
    private final List<BooleanProperty> playerClaimableRoutes
            = new ArrayList<>(Collections.nCopies(ChMap.routes().size(), new SimpleBooleanProperty(false))); //or simple array?


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

    }

    private Map<PlayerId, IntegerProperty> initMap(){
        Map<PlayerId, IntegerProperty> map = new EnumMap<>(PlayerId.class);
        for (PlayerId id : PlayerId.ALL) {
            map.put(id, new SimpleIntegerProperty(0));
        }
        return map;
    }

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
        }

        newPlayerState.tickets().forEach(t -> {
            if (!playerTickets.contains(t)) {
                playerTickets.add(t);
            }
        });
        SortedBag<Card> cards = newPlayerState.cards();
        cards.forEach(c -> playerCardsCount.get(c).set(cards.countOf(c)));

        for (int i = 0; i < ChMap.routes().size(); i++) {
            playerClaimableRoutes.get(i).set(
                    //boolean : three conditions
                    gameState.currentPlayerId() == player
                            && routeOwners.get(i).getValue() == null
                            && newPlayerState.canClaimRoute(ChMap.routes().get(i)));

        }

    }


    public ReadOnlyIntegerProperty ticketAmountProperty() {
        return ticketAmount;
    }


    public ReadOnlyIntegerProperty cardAmountProperty() {
        return cardAmount;
    }

    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    public ReadOnlyObjectProperty<PlayerId> getRouteOwners(Route route) {
        return routeOwners.get(ChMap.routes().indexOf(route));
    }

    public ReadOnlyIntegerProperty getPlayerTicketCount(PlayerId playerId) {
        return playerTicketCount.get(playerId);
    }

    public ReadOnlyIntegerProperty getPlayerCardCount(PlayerId playerId) {
        return playerCardCount.get(playerId);
    }

    public ReadOnlyIntegerProperty getPlayerCarCount(PlayerId playerId) {
        return playerCarCount.get(playerId);
    }

    public ReadOnlyIntegerProperty getPlayerConstructionPoints(PlayerId playerId) {
        return playerConstructionPoints.get(playerId);
    }

    public ObservableList<Ticket> getPlayerTickets() {
        return FXCollections.unmodifiableObservableList(playerTickets);
    }

    public ReadOnlyIntegerProperty getPlayerCardsCount(Card card) {
        return playerCardsCount.get(card);
    }

    public ReadOnlyBooleanProperty getPlayerClaimableRoute(Route route) {
        return playerClaimableRoutes.get(ChMap.routes().indexOf(route));
    }

    private List<ObjectProperty<Card>> createFaceUpCards() {
        List<ObjectProperty<Card>> list = new ArrayList<>();
        for (int i = 0; i < FACE_UP_CARDS_COUNT; i++) {
            list.add(new SimpleObjectProperty<>());
        }
        return list;
    }

    public boolean canDrawTickets() {
        return gameState.canDrawTickets();
    }

    public boolean canDrawCards() {
        return gameState.canDrawCards();
    }

    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return currentPlayerState.possibleClaimCards(route);
    }
}
