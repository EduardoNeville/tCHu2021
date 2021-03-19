package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Class PlayerState
 *
 * Methods: tickets, withAddedTickets, cards, withAddedCard, withAddedCards, canClaimRoute,
 * possibleClaimCards, possibleAdditionalCards, withClaimedRoute, ticketPoints, finalPoints
 *
 * @author Eduardo Neville (314667)
 */
public class PlayerState extends PublicPlayerState{

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    private final List<Route> routes;

    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes){
        super(tickets.size(),cards.size(),routes);
        this.tickets = tickets;
        this.cards = cards;
        this.routes = List.copyOf(routes);
    }

    //TODO create the initial conditions for the players
    public static PlayerState initial(SortedBag<Card> initialCards){
        Preconditions.checkArgument(!(initialCards.size() == Constants.INITIAL_CARDS_COUNT));
        return new PlayerState(SortedBag.of(), initialCards,List.of());
    }


    public SortedBag<Ticket> tickets(){
        return tickets;
    }

    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets){
        return new PlayerState(tickets.union(newTickets),cards,routes);
    }

    public SortedBag<Card> cards(){
        return cards;
    }

    public PlayerState withAddedCard(Card card){
        return new PlayerState(tickets,cards.union(SortedBag.of(card)),routes);
    }

    public PlayerState withAddedCards(SortedBag<Card> additionalCards){
        return new PlayerState(tickets, cards.union(additionalCards),routes);
    }

    public boolean canClaimRoute(Route route){
        return !(route.possibleClaimCards().isEmpty());
    }

    public List<SortedBag<Card>> possibleClaimCards(Route route){
        Preconditions.checkArgument(route.length()> cards.size());
        return route.possibleClaimCards(); //is this correct? Used method from Route
    }

    //TODO
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount,
                                                         SortedBag<Card> initialCards,
                                                         SortedBag<Card> drawnCards){
        //1 Calculate usable cards
        SortedBag<Card> options1 = null;

        for (Card card: SortedBag.of(cards.difference(initialCards))) {
            for (Card card1 : drawnCards) {
                if (card.color().equals(card1.color())||card.color().equals(Card.LOCOMOTIVE)) {
                    options1.add(SortedBag.of(card));
                }
            }
        }
        //2 create all subsets of
        //we need to use subsetsOfSize
        List<SortedBag<Card>> options = new ArrayList<>();
        //use subsetOfSize from SortedBag

        //3 sort them with amount of LOCOMOTIVE cards
        options.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
        return options;
    }


    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
        Preconditions.checkArgument(!(cards.size()<route.length()));
        List<Route> totalRoutes = new ArrayList<>(routes);
        totalRoutes.add(route);
        return new PlayerState(tickets,cards.difference(claimCards),totalRoutes);
    }

    public int ticketPoints(){
        //create stationPartion give it to ticket points
        int maxID = 0;
        for (Route route: routes) {
            if (route.length()> maxID){
                maxID = route.length();
            }
        }
        //use max+1 to build stationPartition
        var networkStation = new StationPartition.Builder(maxID+1);
        for (Route route: routes) {
            networkStation.connect(route.station1(),route.station2());
        }
        //for loop ticket for points
        int maxTicketPoints = 0;
        for (Ticket ticket: tickets) {
            maxTicketPoints = maxTicketPoints + ticket.points(networkStation.build());
        }
        return maxTicketPoints;
    }

    public int finalPoints(){
        return claimPoints() + ticketPoints();
    }
}
