package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

public interface ActionHandler { //TODO does it have a <T extends Events> ?

    //TODO do these interfaces need to be abstract, public or both?

    interface DrawTicketsHandler{
        void onDrawTickets();
    }

    interface DrawCardHandler{
        void onDrawCard(); //card slot
    }

    interface ClaimRouteHandler{
        void onClaimedRoute(Route route, SortedBag<Card> cardDeck); //TODO Card or CardState?
    }

    interface ChooseTicketsHandler{
        void onChooseTickets(SortedBag<Ticket> ticketDeck);
    }

    interface ChooseCardsHandler{
        void onChooseCards(SortedBag<Card> cardDeck);
    }



}
