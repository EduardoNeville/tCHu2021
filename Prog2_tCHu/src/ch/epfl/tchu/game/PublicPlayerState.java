package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

public class PublicPlayerState {
    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;

    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes){
        Preconditions.checkArgument(!(ticketCount<0));
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);
    }

    public int ticketCount() {
        return ticketCount;
    }

    public int CardCount() {
        return cardCount;
    }

    public List<Route> routes() {
        return routes;
    }

    public int carCount(){
        return
    }
    public int getWagonCount = carCount();

    public int claimPoints(){
        routes.size();
    }

}
