package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.ActionHandler;

import java.util.List;
import java.util.Map;

/**
 * Player interface that serves to communicate information to the player of to get his game choices.
 *
 * @author Martin Sanchez Lopez (313238)
 */
public interface Player {

    /**
     * A turn kind. The different actions a player can choose to do on his turn.
     */
    enum TurnKind {
        DRAW_TICKETS, DRAW_CARDS, CLAIM_ROUTE;

        public final static List<TurnKind> ALL = List.of(values());
    }

    /**
     * Communicates to the Player his id and the names of all the players (himself included).
     *
     * @param ownId       id of the player
     * @param playerNames map of all the player names
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * Communicates to the Player the initial tickets he was given.
     *
     * @param tickets the tickets given
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * Communicates to the player an info.
     *
     * @param info String of the info to give to the player
     */
    void receiveInfo(String info);

    /**
     * Communicates to the player a change in the state of the game and his own state.
     *
     * @param newState new public game state
     * @param ownState new player state of the player
     */
    void updateState(PublicGameState newState, PlayerState ownState);


    /**
     * Ask the players the tickets they want to keep from the tickets given as the game initialises and returns
     * a bag of the tickets kept.
     *
     * @return bag of the tickets kept
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * Ask the player what type of play he wants to do during the turn and return it.
     *
     * @return the turn kind the player will do this turn
     */
    TurnKind nextTurn();

    /**
     * Ask the player the tickets he want to keep when he draws tickets midgame and returns them as a bag.
     *
     * @param options the tickets the player can choose from
     * @return the tickets the player wants to keep
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * Ask the player where the player wants to draw from when he chooses to draw. Returns an int of the
     * face up card he wants or -1 for a card from the deck.
     *
     * @return the face up card slot or -1 if he wants to draw from the deck
     */
    int drawSlot();

    /**
     * Asks the player which route he want to try to claim and returns it.
     *
     * @return the route the player wants to claim
     */
    Route claimedRoute();


    /**
     * Asks the player the initial cards he wants to use to try to claim a route and returns them.
     *
     * @return the initial cards the player uses to try to claim a route
     */
    SortedBag<Card> initialClaimCards();


    /**
     * Aks the player to pick which cards to use in order to finalize claiming a tunnel route and returns
     * a bag of the extra cards he choose. If the player choose to not claim or can't claim with his cards
     * function returns an empty bag.
     *
     * @param options list of options the player can choose from to finalise claim
     * @return SortedBag of the additional cards he uses to claim tunnel or an empty bag if he doesn't claim
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);


}
