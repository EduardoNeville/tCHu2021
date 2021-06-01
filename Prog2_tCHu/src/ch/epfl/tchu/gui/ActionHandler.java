package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.ChatMessage;

/**
 * Interface that has different functional interfaces intended as handlers for various game actions.
 *
 * @author Eduardo Neville (314677)
 */
public interface ActionHandler {

    /**
     * Action handler for drawing tickets
     */
    interface DrawTicketsHandler{
        void onDrawTickets();
    }

    /**
     * Action handler for drawing cards
     */
    interface DrawCardHandler{
        void onDrawCard(int slot); //0-4 or -1 for deck
    }

    /**
     * Action handler for claiming routes
     */
    interface ClaimRouteHandler{
        void onClaimRoute(Route route, SortedBag<Card> cardDeck);
    }

    /**
     * Action handler for choosing tickets
     */
    interface ChooseTicketsHandler{
        void onChooseTickets(SortedBag<Ticket> tickets);
    }

    /**
     * Action handler for choosing cards
     */
    interface ChooseCardsHandler{
        void onChooseCards(SortedBag<Card> cards);
    }

    interface TradeDealHandler{
        void onDealOffer(TradeDeal tradeDeal);
    }


    interface ChatHandler{
        void onSend(ChatMessage message);
    }



}
