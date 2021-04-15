package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * Class PublicPlayerState
 * Methods: ticketCount, CardCount, routes, carCount, claimPoints.
 *
 * @author Eduardo Neville (314667)
 */
public class PublicPlayerState {
    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;

    /**
     * Constructor for PublicPlayerState
     * @param ticketCount
     *              # of tickets a player has
     * @param cardCount
     *              # of cards a player has
     * @param routes
     *              # of routes a player has
     * @throws IllegalArgumentException
     *              if ticketCount is smaller that 0 and cardCount is smaller than 0
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes){
        Preconditions.checkArgument(!(ticketCount<0) && cardCount>=0);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);
    }

    /**
     * Ticket getter
     * @return # of tickets
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * Card getter
     * @return # of cards
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * Routes getter
     * @return List of routes
     */
    public List<Route> routes() {
        return routes;
    }

    /**
     * Wagon getter
     * @return # of wagons
     */
    public int carCount(){
        int total =0;
        for (Route route: routes) {
            total = total + route.length();
        }
        return Constants.INITIAL_CAR_COUNT - total;
    }

    /**
     * Points earned
     * @return # of points earned
     */
    public int claimPoints(){
        int points = 0;

        for (Route r :
                routes()) {
            points += Constants.ROUTE_CLAIM_POINTS.get(r.length());
        }
        return points;
    }
}
