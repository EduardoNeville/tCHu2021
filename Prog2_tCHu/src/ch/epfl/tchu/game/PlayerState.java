package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.List;

public class PlayerState extends PublicPlayerState{

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    private final List<Route> routes;

    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes){
        super(tickets.size(),cards.size(),routes );
        this.tickets = tickets;
        this.cards = cards;
        this.routes = List.copyOf(routes);
    }

    public static PlayerState initial(SortedBag<Card> initialCards){
        Preconditions.checkArgument(initialCards.isEmpty());

    }

    public SortedBag<Ticket> tickets(){
        return tickets;
    }

    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets){
        return new PlayerState(tickets.union(newTickets), cards,routes);
    }

    public SortedBag<Card> cards(){
        return cards;
    }

    //TODO
    public PlayerState withAddedCard(Card card){
        //SortedBag<Card> AddedCards = List.copyOf(card);

        return new PlayerState(tickets,cards.union(card),routes);
    }

    public PlayerState withAddedCards(SortedBag<Card> additionalCards){
        return new PlayerState(tickets, cards.union(additionalCards),routes);
    }

    public boolean canClaimRoute(Route route){
        return !(route.possibleClaimCards().isEmpty());
    }

    //TODO find way to use route method possibleClaimCards
    public List<SortedBag<Card>> possibleClaimCards(Route route){

    }

    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount,
                                                         SortedBag<Card> initialCards,
                                                         SortedBag<Card> drawnCards){


    }

    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){

    }

    public int ticketPoints(){
        return
    }

    public int finalPoints(){
        return 
    }
}
