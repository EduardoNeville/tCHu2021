package ch.epfl.tchu.net;

/**
 * Enum of all the types of messages that the server and clients can use to communicate
 *
 * @author Martin Sanchez Lopez (313238)
 */
public enum MessageId {
    INIT_PLAYERS, RECEIVE_INFO, UPDATE_STATE, SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS, NEXT_TURN, CHOOSE_TICKETS, DRAW_SLOT,
    ROUTE, CARDS, CHOOSE_ADDITIONAL_CARDS

}
