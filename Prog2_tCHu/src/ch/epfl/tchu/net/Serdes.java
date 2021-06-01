package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Various serdes for the game of tchu.
 *
 * @author Eduardo Neville (314677)
 */
public final class Serdes {

    /**
     * Integer serde
     */
    public static final Serde<Integer> INTEGER_SERDE = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt);

    public static final Serde<Boolean> BOOLEAN_SERDE = Serde.of(
            bool -> {
                if(bool) return INTEGER_SERDE.serialize(1);
                else return INTEGER_SERDE.serialize(0);
            },
            msg -> {
                int res = INTEGER_SERDE.deserialize(msg);
                return res == 1;
            });

    /**
     * String serde
     */
    public static final Serde<String> STRING_SERDE = Serde.of(
            string -> Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8)),
            ((base64 -> new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8)))
    );

    /**
     * PlayerId serde
     */
    public static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);

    /**
     * TurnKind serde
     */
    public static final Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);

    /**
     * Card Serde
     */
    public static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);

    /**
     * Route serde
     */
    public static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());

    /**
     * Ticket serde
     */
    public static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());


    /**
     * List of strings serde
     */
    public static final Serde<List<String>> LIST_String_SERDE = Serde.listOf(STRING_SERDE, ",");

    /**
     * List of cards serde
     */
    public static final Serde<List<Card>> LIST_CARD_SERDE = Serde.listOf(CARD_SERDE, ",");

    /**
     * list of routes serde
     */
    public static final Serde<List<Route>> LIST_ROUTE_SERDE = Serde.listOf(ROUTE_SERDE, ",");

    /**
     * bag of cards serde
     */
    public static final Serde<SortedBag<Card>> SORTED_BAG_CARD_SERDE = Serde.bagOf(CARD_SERDE, ",");

    /**
     * bag of tickets serde
     */
    public static final Serde<SortedBag<Ticket>> SORTED_BAG_TICKET_SERDE = Serde.bagOf(TICKET_SERDE, ",");

    /**
     * list of bags of cards serde
     */
    public static final Serde<List<SortedBag<Card>>> LIST_SORTED_BAG_CARD_SERDE = Serde.listOf(SORTED_BAG_CARD_SERDE, ";");

    /**
     * Public card state serde
     */
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = Serde.of(
            object -> String.format("%s;%s;%s",
                    LIST_CARD_SERDE.serialize(object.faceUpCards()),
                    INTEGER_SERDE.serialize(object.deckSize()),
                    INTEGER_SERDE.serialize(object.discardsSize())),

            object -> {
                String[] a = object.split(Pattern.quote(";"), -1);
                return new PublicCardState(LIST_CARD_SERDE.deserialize(a[0]),
                        INTEGER_SERDE.deserialize(a[1]),
                        INTEGER_SERDE.deserialize(a[2]));
            });

    /**
     * Trade Deal Serde
     */
    public static final Serde<TradeDeal> TRADE_DEAL_SERDE = Serde.of(
            d -> String.format("%s;%s;%s;%s;%s;%s",
                    ROUTE_SERDE.serialize(d.routeReceive()),
                    SORTED_BAG_CARD_SERDE.serialize(d.cardsReceive()),
                    TICKET_SERDE.serialize(d.ticketsReceive()),
                    ROUTE_SERDE.serialize(d.routeGive()),
                    SORTED_BAG_CARD_SERDE.serialize(d.cardsGive()),
                    TICKET_SERDE.serialize(d.ticketsGive())),

            d -> {
                String[] a = d.split(Pattern.quote(";"), -1);
                return new TradeDeal(ROUTE_SERDE.deserialize(a[0]),
                        SORTED_BAG_CARD_SERDE.deserialize(a[2]),
                        TICKET_SERDE.deserialize(a[1]),
                        ROUTE_SERDE.deserialize(a[3]),
                        SORTED_BAG_CARD_SERDE.deserialize(a[5]),
                        TICKET_SERDE.deserialize(a[4]));
            });

    /**
     * Public card state serde
     */
    public static final Serde<ChatMessage> CHAT_MESSAGE_SERDE = Serde.of(
            message -> String.format("%s;%s",
                    STRING_SERDE.serialize(message.message()),
                    PLAYER_ID_SERDE.serialize(message.senderId())),

            message -> {
                String[] a = message.split(Pattern.quote(";"), -1);
                return new ChatMessage(STRING_SERDE.deserialize(a[0]),
                        PLAYER_ID_SERDE.deserialize(a[1]));
            });


    /**
     * public player state serde
     */
    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = Serde.of(
            object -> String.format("%s;%s;%s",
                    INTEGER_SERDE.serialize(object.ticketCount()),
                    INTEGER_SERDE.serialize(object.cardCount()),
                    LIST_ROUTE_SERDE.serialize(object.routes())),

            object -> {
                String[] a = object.split(Pattern.quote(";"), -1);
                return new PublicPlayerState(INTEGER_SERDE.deserialize(a[0]),
                        INTEGER_SERDE.deserialize(a[1]),
                        LIST_ROUTE_SERDE.deserialize(a[2]));
            });

    /**
     * player state serde
     */
    public static final Serde<PlayerState> PLAYER_STATE_SERDE = Serde.of(
            object -> String.format("%s;%s;%s",
                    SORTED_BAG_TICKET_SERDE.serialize(object.tickets()),
                    SORTED_BAG_CARD_SERDE.serialize(object.cards()),
                    LIST_ROUTE_SERDE.serialize(object.routes())),

            object -> {
                String[] a = object.split(Pattern.quote(";"), -1);

                return new PlayerState(SORTED_BAG_TICKET_SERDE.deserialize(a[0]),
                        SORTED_BAG_CARD_SERDE.deserialize(a[1]),
                        LIST_ROUTE_SERDE.deserialize(a[2]));
            });

    /**
     * public game state serde
     */
    public static final Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = Serde.of(
            object -> String.format("%s:%s:%s:%s:%s:%s",
                    INTEGER_SERDE.serialize(object.ticketsCount()),
                    PUBLIC_CARD_STATE_SERDE.serialize(object.cardState()),
                    PLAYER_ID_SERDE.serialize(object.currentPlayerId()),
                    PUBLIC_PLAYER_STATE_SERDE.serialize(object.playerState(PlayerId.PLAYER_1)),
                    PUBLIC_PLAYER_STATE_SERDE.serialize(object.playerState(PlayerId.PLAYER_2)),
                    PLAYER_ID_SERDE.serialize(object.lastPlayer())),

            object -> {
                String[] a = object.split(Pattern.quote(":"), -1);
                Map<PlayerId, PublicPlayerState> playerStateMap = new EnumMap<>(PlayerId.class);
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
