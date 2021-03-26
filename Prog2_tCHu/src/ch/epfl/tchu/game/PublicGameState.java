package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
//import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Copy; what is this?????

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * PublicGameState
 *
 * Methods: ticketCount,canDrawTickets, cardState, canDrawCards, currentPlayerId, playerState,
 * currentPlayerState,claimedRoutes,lastPlayer
 *
 * @author Eduardo Neville (314667)
 */
public class PublicGameState {
    private final int ticketCounT;
    private final PublicCardState cardStatE;
    private final PlayerId currentPlayerID;
    private final  Map<PlayerId, PublicPlayerState> playerStatE;
    private final PlayerId lastPlayeR;

    /**
     * Constructor for PublicGameState
     * @param ticketsCount # of tickets
     * @param cardState cards on the deck
     * @param currentPlayerId current player
     * @param playerState Map of playerId and PlayerState
     * @param lastPlayer last Player
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState,
                            PlayerId currentPlayerId,
                           Map<PlayerId,PublicPlayerState> playerState,
                           PlayerId lastPlayer){

        if(cardState == null || currentPlayerId == null || playerState == null)
            throw new NullPointerException();

        Preconditions.checkArgument(ticketsCount>=0);
        Preconditions.checkArgument(playerState.size()==2);



        this.ticketCounT = ticketsCount;
        this.cardStatE = Objects.requireNonNull(cardState);
        this.currentPlayerID = Objects.requireNonNull(currentPlayerId);
        this.playerStatE = Map.copyOf(playerState);
        this.lastPlayeR = lastPlayer; //can be null
    }

    /**
     * Ticket size
     * @return ticket size
     */
    public int ticketCount(){
        return ticketCounT;
    }

    /**
     * checks if you can draw a ticket
     * @return checks if you can draw a ticket
     */
    public boolean canDrawTickets(){
        return !(ticketCounT ==0);
    }

    /**
     *
     * @return
     */
    public PublicCardState cardState(){
        return new PublicCardState(cardStatE.faceUpCards(), cardStatE.deckSize(), cardStatE.discardsSize());
    }

    /**
     * 
     * @return
     */
    public boolean canDrawCards(){
        return (cardStatE.deckSize()+cardStatE.discardsSize()>Constants.INITIAL_TICKETS_COUNT);
    }

    /**
     * Getter for the current PlayerId
     * @return current PlayerId
     */
    public PlayerId currentPlayerId(){
        return currentPlayerID;
    }

    public PublicPlayerState playerState(PlayerId playerId){
        return new PublicPlayerState(ticketCounT, cardStatE.totalSize(),playerState(playerId).routes());
    }

    /**
     * Getter for the currentplayerState
     * @return the playerState of the currentplayer
     */
    public PublicPlayerState currentPlayerState(){
        return playerState(currentPlayerID);
    }

    /**
     * Gives the claimedRoutes of a player
     * @return returns the routes of a player
     */
    public List<Route> claimedRoutes(){
        List<PublicPlayerState> allPublicPlayerState = new ArrayList<PublicPlayerState>(playerStatE.values());
        List<Route> allRoutes = new ArrayList<>();

        for (PublicPlayerState player: allPublicPlayerState) {
            allRoutes.addAll(player.routes());
        }
        return allRoutes;
    }

    /**
     * Getter for the lastPlayer
     * @return last player
     */
    public PlayerId lastPlayer(){
        return lastPlayeR;
    }
}
