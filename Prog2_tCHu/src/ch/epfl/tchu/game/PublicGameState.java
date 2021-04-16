package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;

/**
 * Class that represents the state of the game accessible to all players.
 * <p>
 * Methods: ticketCount,canDrawTickets, cardState, canDrawCards, currentPlayerId, playerState,
 * currentPlayerState,claimedRoutes,lastPlayer
 *
 * @author Eduardo Neville (314667)
 */
public class PublicGameState {
    private final int ticketCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerID;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * Constructor for PublicGameState
     *
     * @param ticketsCount    # of tickets
     * @param cardState       cards on the deck
     * @param currentPlayerId current player
     * @param playerState     Map of playerId and PlayerState
     * @param lastPlayer      last Player
     * @throws IllegalArgumentException thrown if ticketCount is smaller that 0
     * @throws IllegalArgumentException thrown if playerState size isn't the size of PlayerId.COUNT
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState,
                           PlayerId currentPlayerId,
                           Map<PlayerId, PublicPlayerState> playerState,
                           PlayerId lastPlayer) {

        Preconditions.checkArgument(ticketsCount >= 0);
        Preconditions.checkArgument(playerState.size() == PlayerId.COUNT);

        this.ticketCount = ticketsCount;
        this.cardState = Objects.requireNonNull(cardState);
        this.currentPlayerID = Objects.requireNonNull(currentPlayerId);
        this.playerState = Map.copyOf(Objects.requireNonNull(playerState));
        this.lastPlayer = lastPlayer;
    }

    /**
     * Returns the amount of tickets there are.
     *
     * @return amount of tickets there are
     */
    public int ticketsCount() {
        return ticketCount;
    }

    /**
     * Checks if you can draw a ticket
     *
     * @return true if you can draw a ticket
     */
    public boolean canDrawTickets() {
        return !(ticketCount == 0);
    }

    /**
     * Returns the public card state of the game.
     *
     * @return the public card state of the game
     */
    public PublicCardState cardState() {
        return cardState;
    }

    /**
     * Checks if cards can be drawn by checking the cardState is bigger that the initial ticket count.
     *
     * @return true if you can draw a card
     */
    public boolean canDrawCards() {
        return (cardState.deckSize() + cardState.discardsSize() >= 5);
    }

    /**
     * Returns the PlayerId of the current player.
     *
     * @return the PlayerId of the current player
     */
    public PlayerId currentPlayerId() {
        return currentPlayerID;
    }

    /**
     * Returns the publicPlayerState of a player.
     *
     * @param playerId the playerId of the player we want
     * @return publicPlayerState of the player gave the id of
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * Returns the publicPlayerState of the current player.
     *
     * @return the publicPlayerState of the current player
     */
    public PublicPlayerState currentPlayerState() {
        return playerState(currentPlayerId());
    }

    /**
     * Gives the claimedRoutes of a player
     *
     * @return returns list of the routes of the players
     */
    public List<Route> claimedRoutes() {
        List<PublicPlayerState> allPublicPlayerState = new ArrayList<>(playerState.values());
        List<Route> allRoutes = new ArrayList<>();

        for (PublicPlayerState player : allPublicPlayerState) {
            allRoutes.addAll(player.routes());
        }
        return allRoutes;
    }

    /**
     * Getter for the lastPlayer
     *
     * @return player id of the last player
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }
}
