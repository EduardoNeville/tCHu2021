package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Serdes
 *
 * @author Eduardo Neville (314677)
 */
public class Serdes {

    //Part 1

    public static final Serde<Integer> INTEGER_SERDE = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt);

    public static final Serde<String> STRING_SERDE = Serde.of(); //TODO

    public static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);

    public static final Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);

    public static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);

    public static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());

    public static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    //Part 2

    public static final Serde<List<String>> LIST_String_SERDE = Serde.listOf(STRING_SERDE, " , ");

    public static final Serde<List<Card>> LIST_CARD_SERDE = Serde.listOf(CARD_SERDE, " , ");

    public static final Serde<List<Route>> LIST_ROUTE_SERDE = Serde.listOf(ROUTE_SERDE, " , ");

    public static final Serde<SortedBag<Card>> SORTED_BAG_CARD_SERDE = Serde.bagOf(CARD_SERDE, " , ");

    public static final Serde<SortedBag<Ticket>> SORTED_BAG_TICKET_SERDE = Serde.bagOf(TICKET_SERDE, " , ");

    public static final Serde<List<SortedBag<Card>>> LIST_SORTED_BAG_CARD_SERDE = Serde.listOf(SORTED_BAG_CARD_SERDE, " ; ");

    // PublicCardState
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = new Serde<PublicCardState>() {
        @Override
        public String serialize(PublicCardState object) {
            List<String> faceUpCardsStr = new ArrayList<>();
            object.faceUpCards().forEach(o -> faceUpCardsStr.add(CARD_SERDE.serialize(o)));

            return String.join(" ; ",
                    LIST_CARD_SERDE.serialize(object.faceUpCards()) +
                            INTEGER_SERDE.serialize(object.deckSize()) +
                            INTEGER_SERDE.serialize(object.discardsSize()));
            //return a string of all the different parts of the publicCardState with ; in between them
        }
//basically use the other serialized from serde and join them all into one string after to return
        @Override
        public PublicCardState deserialize(String string) {
            LIST_CARD_SERDE.deserialize(string);
            INTEGER_SERDE.deserialize();
            return
        }
    };
    //here the list can be any list of cards

    // PublicPlayerState
    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = new Serde<PublicPlayerState>() {
        @Override
        public String serialize(PublicPlayerState object) {
            return String.join(" ; ",
                    INTEGER_SERDE.serialize(object.ticketCount()) +
                                INTEGER_SERDE.serialize(object.cardCount()) +
                                LIST_ROUTE_SERDE.serialize(object.routes()));
            //return a string of all the different parts of the publicCardState with ; in between them
        }
        @Override
        public PublicPlayerState deserialize(String string) {

            return string.get
        }
    };

    public static final Serde<PlayerState> PLAYER_STATE_SERDE = new Serde<PlayerState>() {
        @Override
        public String serialize(PlayerState object) {

            return String.join(" ; ",
                    SORTED_BAG_TICKET_SERDE.serialize(object.tickets()) +
                            SORTED_BAG_CARD_SERDE.serialize(object.cards()) +
                            LIST_ROUTE_SERDE.serialize(object.routes()));
        }

        @Override
        public PlayerState deserialize(String string) {

            return string.get
        }
    };

    public static final Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = new Serde<PublicGameState>() {
        @Override
        public String serialize(PublicGameState object) {

            return String.join(" : ",
                    object.ticketsCount() +
                            PUBLIC_CARD_STATE_SERDE.serialize(object.cardState()) +
                            PLAYER_ID_SERDE.serialize(object.currentPlayerId()) +
                            PUBLIC_PLAYER_STATE_SERDE.serialize(object.playerState(PlayerId.PLAYER_1))+
                            PUBLIC_PLAYER_STATE_SERDE.serialize(object.playerState(PlayerId.PLAYER_2)));
        }

        @Override
        public PublicGameState deserialize(String string) {
            return null;
        }
    };
}
