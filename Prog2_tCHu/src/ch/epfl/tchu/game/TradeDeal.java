package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

import java.util.Set;

public class TradeDeal {
    private final SortedBag<Card> cardsGive;
    private final SortedBag<Card> cardsReceive;
    private final Ticket ticketsReceive;
    private final Ticket ticketsGive;
    private final Route routeReceive;
    private final Route routeGive;

    public TradeDeal(Route routeReceive, SortedBag<Card> cardsReceive, Ticket ticketsReceive,
                     Route routeGive, SortedBag<Card> cardsGive, Ticket ticketsGive) {
        this.cardsGive = cardsGive;
        this.cardsReceive = cardsReceive;
        this.ticketsReceive = ticketsReceive;
        this.ticketsGive = ticketsGive;
        this.routeReceive = routeReceive;
        this.routeGive = routeGive;
    }

    public SortedBag<Card> cardsGive() {
        return cardsGive;
    }

    public SortedBag<Card> cardsReceive() {
        return cardsReceive;
    }

    public Ticket ticketsReceive() {
        return ticketsReceive;
    }

    public Ticket ticketsGive() {
        return ticketsGive;
    }

    public Route routeReceive() {
        return routeReceive;
    }

    public Route routeGive() {
        return routeGive;
    }

}
