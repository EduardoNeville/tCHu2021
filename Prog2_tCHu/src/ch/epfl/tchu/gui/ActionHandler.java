package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

public interface ActionHandler { //TODO does it have a <T extends Events> ?

    //TODO do these interfaces need to be abstract, public or both?
    // -> They are both by default

    interface DrawTicketsHandler{
        void onDrawTickets();
    }

    interface DrawCardHandler{
        void onDrawCard(int slot); //0-4 or -1 for deck
    }

    interface ClaimRouteHandler{
        void onClaimRoute(Route route, SortedBag<Card> cardDeck);
    }

    interface ChooseTicketsHandler{
        void onChooseTickets(SortedBag<Ticket> tickets);
    }

    interface ChooseCardsHandler{
        void onChooseCards(SortedBag<Card> cards);
    }



}
