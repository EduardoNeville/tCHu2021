package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PublicGameState {
    private final int ticketCounT;
    private final PublicCardState cardStatE;
    private final PlayerId currentPlayerID;
    private final  Map<PlayerId, PublicPlayerState> playerStatE;
    private final PlayerId lastPlayeR;


    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer){
        Preconditions.checkArgument(ticketsCount<0);
        Preconditions.checkArgument(playerState.containsKey(2) && playerState.containsValue(2));
        //throw nullPointerException if rest of arguments null
        this.ticketCounT = ticketsCount;
        this.cardStatE = cardState;
        this.currentPlayerID = currentPlayerId;
        this.playerStatE = playerState;
        this.lastPlayeR = lastPlayer; //can be null
    }

    public int ticketCount(){
        return ticketCounT;
    }

    public boolean canDrawTickets(){
        return !(ticketCounT ==0);
    }

    public PublicCardState cardState(){
        return new PublicCardState(cardStatE.faceUpCards(), cardStatE.deckSize(), cardStatE.discardsSize());
    }

    public boolean canDrawCards(){
        //Correct use of constants?
        return (cardStatE.deckSize()+cardStatE.discardsSize()>Constants.INITIAL_TICKETS_COUNT);
    }

    public PlayerId currentPlayerId(){
        return currentPlayerID;
    }

    public PublicPlayerState playerState(PlayerId playerId){
        return new PublicPlayerState(ticketCounT, cardStatE.totalSize(),playerState(playerId).routes());
    }

    public PublicPlayerState currentPlayerState(){
        return playerState(currentPlayerID);
    }

    public List<Route> claimedRoutes(){
        List<PublicPlayerState> allPublicPlayerState = new ArrayList<PublicPlayerState>(playerStatE.values());
        List<Route> allRoutes = new ArrayList<>();

        for (PublicPlayerState player: allPublicPlayerState) {
            allRoutes.addAll(player.routes());
        }
        return allRoutes;
    }

    public PlayerId lastPlayer(){
        return lastPlayeR;
    }
}
