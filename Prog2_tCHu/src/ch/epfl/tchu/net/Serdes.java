package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Serdes class
 *
 * @author Eduardo Neville (314677)
 */
public class Serdes{

    //Part 1
    private static final Serde<Integer> INTEGER_SERDE = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt);

    private static final Serde<String> STRING_SERDE = Serde.of(
            string -> Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8)),
            ((base64 -> new String(Base64.getDecoder().decode(base64),StandardCharsets.UTF_8)))
    );

    private static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);

    private static final Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);

    private static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);

    private static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());

    private static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    //Part 2

    private static final Serde<List<String>> LIST_String_SERDE = Serde.listOf(STRING_SERDE, " , ");

    private static final Serde<List<Card>> LIST_CARD_SERDE = Serde.listOf(CARD_SERDE, " , ");

    private static final Serde<List<Route>> LIST_ROUTE_SERDE = Serde.listOf(ROUTE_SERDE, " , ");

    private static final Serde<SortedBag<Card>> SORTED_BAG_CARD_SERDE = Serde.bagOf(CARD_SERDE, " , ");

    private static final Serde<SortedBag<Ticket>> SORTED_BAG_TICKET_SERDE = Serde.bagOf(TICKET_SERDE, " , ");

    private static final Serde<List<SortedBag<Card>>> LIST_SORTED_BAG_CARD_SERDE = Serde.listOf(SORTED_BAG_CARD_SERDE, " ; ");

    //PublicCardState
    private static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = Serde.of(
            object -> String.format("%s:%s:%s",
                    LIST_CARD_SERDE.serialize(object.faceUpCards()),
                    INTEGER_SERDE.serialize(object.deckSize()),
                    INTEGER_SERDE.serialize(object.discardsSize())),

            object -> {
                String[] a = object.split(Pattern.quote(object), -1);
                return new PublicCardState(LIST_CARD_SERDE.deserialize(a[0]),
                                    INTEGER_SERDE.deserialize(a[1]),
                                INTEGER_SERDE.deserialize(a[2]));
            });

    //PublicPlayerState
    private static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = Serde.of(
            object -> String.format("%s:%s:%s",
                    INTEGER_SERDE.serialize(object.ticketCount()) ,
                    INTEGER_SERDE.serialize(object.cardCount()) ,
                    LIST_ROUTE_SERDE.serialize(object.routes())),

            object -> {
                String[] a = object.split(Pattern.quote(object), -1);
                return new PublicPlayerState(INTEGER_SERDE.deserialize(a[0]),
                        INTEGER_SERDE.deserialize(a[1]),
                        LIST_ROUTE_SERDE.deserialize(a[2]));
            });

    //PlayerState
    private static final Serde<PlayerState> PLAYER_STATE_SERDE = Serde.of(
            object -> String.format("%s:%s:%s",
                    SORTED_BAG_TICKET_SERDE.serialize(object.tickets()),
                    SORTED_BAG_CARD_SERDE.serialize(object.cards()),
                    LIST_ROUTE_SERDE.serialize(object.routes())),

            object -> {
                String[] a = object.split(Pattern.quote(object),-1);
                return new PlayerState(SORTED_BAG_TICKET_SERDE.deserialize(a[0]),
                        SORTED_BAG_CARD_SERDE.deserialize(a[1]),
                        LIST_ROUTE_SERDE.deserialize(a[2]));
            });

    //Public Game State
    private static final Serde<PublicGameState> PUBLIC_GAME_STATE_SERDES = Serde.of(
            object -> String.format("%s:%s:%s:%s:%s:%s",
                    INTEGER_SERDE.serialize(object.ticketsCount()) ,
                    PUBLIC_CARD_STATE_SERDE.serialize(object.cardState()),
                    PLAYER_ID_SERDE.serialize(object.currentPlayerId()),
                    PUBLIC_PLAYER_STATE_SERDE.serialize(object.playerState(PlayerId.PLAYER_1)),
                    PUBLIC_PLAYER_STATE_SERDE.serialize(object.playerState(PlayerId.PLAYER_2)),
                    PLAYER_ID_SERDE.serialize(object.lastPlayer())),

            object -> {
                String[] a = object.split(Pattern.quote(object),-1);
                Map<PlayerId,PublicPlayerState> playerStateMap = new EnumMap<>(PlayerId.class);
                playerStateMap.put(PlayerId.PLAYER_1, PUBLIC_PLAYER_STATE_SERDE.deserialize(a[3]));
                playerStateMap.put(PlayerId.PLAYER_2, PUBLIC_PLAYER_STATE_SERDE.deserialize(a[4]));
                PlayerId lastPlayer = (a[5].equals("")) ? null : PLAYER_ID_SERDE.deserialize(a[5]);

                return new PublicGameState(INTEGER_SERDE.deserialize(a[0]),
                        PUBLIC_CARD_STATE_SERDE.deserialize(a[1]),
                        PLAYER_ID_SERDE.deserialize(a[2]),
                        playerStateMap,
                        lastPlayer);
            });
}
