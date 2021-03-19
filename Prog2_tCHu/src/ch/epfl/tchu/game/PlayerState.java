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

    /**
     * Constructor for Player State
     * @param tickets SortedBag of Player tickets
     * @param cards SortedBag of Player cards
     * @param routes SortedBag of Player routes
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes){
        super(tickets.size(),cards.size(),routes);
        this.tickets = tickets;
        this.cards = cards;
        this.routes = List.copyOf(routes);
    }

    /**
     * Method that gives initial Player cards
     * @param initialCards SortedBag of initial PlayerCards
     * @return PlayerState at the beginning
     */
    public static PlayerState initial(SortedBag<Card> initialCards){
        Preconditions.checkArgument((initialCards.size() == Constants.INITIAL_CARDS_COUNT));
        return new PlayerState(SortedBag.of(), initialCards,List.of());
    }

    /**
     * Tickets getter
     * @return SortedBag of Player tickets
     */
    public SortedBag<Ticket> tickets(){
        return tickets;
    }

    /**
     * PlayerState with Tickets added
     * @param newTickets new tickets
     * @return PlayerState with new tickets added
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets){
        return new PlayerState(tickets.union(newTickets),cards,routes);
    }

    /**
     * Card getter
     * @return SortedBag of Player cards
     */
    public SortedBag<Card> cards(){
        return cards;
    }

    /**
    * PlayerState with card added
    * @param card new card
    * @return PlayerState with new card added
    */
    public PlayerState withAddedCard(Card card){
        return new PlayerState(tickets,cards.union(SortedBag.of(card)),routes);
    }

    /**
     * PlayerState with cards added
     * @param additionalCards new cards
     * @return PlayerState with new cards added
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards){
        return new PlayerState(tickets, cards.union(additionalCards),routes);
    }

    /**
     * Used to check if a route can be claimed
     * @param route route in question to see if possible to claim
     * @return If it is possible to claim route
     */
    public boolean canClaimRoute(Route route){
        if(carCount() < route.length())
            return false;
        return !(possibleClaimCards(route).isEmpty());
    }

    /**
     * Possible Card claims
     * @param route route we want to claim
     * @return list of possible ways to claim it
     */
     public List<SortedBag<Card>> possibleClaimCards(Route route){
        Preconditions.checkArgument(carCount() > route.length());
        var routePossible = route.possibleClaimCards();
        List<SortedBag<Card>> possibleCards = new ArrayList<>();

        for (var possibleroutes: routePossible ){
            if (cards.contains(possibleroutes)){
                possibleCards.add(possibleroutes);
            }
        }
        return possibleCards;
    }



    /**
     * Used to see which options we have when trying to claim a route
     * @param additionalCardsCount # of cards needed to claim the route
     * @param initialCards cards used from the players hand
     * @param drawnCards  cards need to claim
     * @return List of all possible combinations of cards that can be used to claim route
     */
     public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount,
                                                         SortedBag<Card> initialCards,
                                                         SortedBag<Card> drawnCards){
        Preconditions.checkArgument(Constants.ADDITIONAL_TUNNEL_CARDS>=additionalCardsCount && additionalCardsCount>= 1);
        Preconditions.checkArgument(!(initialCards.isEmpty()));
        Preconditions.checkArgument(drawnCards.size()== Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(initialCards.toSet().size() <= 2);

        //1 Calculate usable cards
        var options1 = new SortedBag.Builder<Card>();

        for (Card card: SortedBag.of(cards.difference(initialCards))) {
            for (Card card1 : drawnCards) {
                if (card.color().equals(card1.color()) || card.equals(Card.LOCOMOTIVE)) {
                    options1.add(card);
                }
            }
        }

        SortedBag<Card> options2 = options1.build();
        //verify that options2 is at least size additionalCards
        if (options2.size()>=additionalCardsCount){
            List<SortedBag<Card>> options = new ArrayList<>(options2.subsetsOfSize(additionalCardsCount));

            //3 sort them with amount of LOCOMOTIVE cards
            options.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
            return options;
        }
        else return List.of();
    }

    /**
     * Claiming a route and loosing the cards used
     * @param route route claimed
     * @param claimCards Cards used to claim the route
     * @return new PlayerState with claimed route and without cards used to claim
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
        List<Route> totalRoutes = new ArrayList<>(routes);
        totalRoutes.add(route);
        return new PlayerState(tickets,cards.difference(claimCards),totalRoutes);
    }

    /**
     * Getting the ticket points of all the routes that the player has
     * @return points
     */
    public int ticketPoints(){
        //create stationPartion give it to ticket points
        int maxID = 0;
        for (Route route: routes) {
            if (route.station1().id() > maxID){
                maxID = route.station1().id();
            }
            if (route.station2().id() > maxID){
                maxID = route.station2().id();
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

    /**
     * Final tally of all Player points
     * @return final Player points
     */
    public int finalPoints(){
        return claimPoints() + ticketPoints();
    }
}
