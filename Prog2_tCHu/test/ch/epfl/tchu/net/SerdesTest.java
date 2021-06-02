package ch.epfl.tchu.net;


import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import static ch.epfl.tchu.game.Card.*;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static org.junit.jupiter.api.Assertions.*;

class SerdesTest {

    @Test
    void integerSerdeWords(){
        int number = 135;
        Serde<Integer> s = Serdes.INTEGER_SERDE;
        assertEquals("135", s.serialize(number));
        assertEquals(135, s.deserialize(s.serialize(number)));
    }

    @Test
    void stringSerdeWorks(){
        String string = "Test String ";

        Serde<String> s = Serdes.STRING_SERDE;
        assertEquals("Test String ", s.deserialize(s.serialize(string)));
        assertEquals("Les méthodes getEncoder et getDecoder de la classe Base64 du paquetage",
                s.deserialize(s.serialize("Les méthodes getEncoder et getDecoder de la classe Base64 du paquetage")));
        assertEquals("À partir de cette étape, aucun fichier de vérification de signatures ne vous est fourni, étant donné que la signature des différentes méthodes",
                s.deserialize(s.serialize("À partir de cette étape, aucun fichier de vérification de signatures ne vous est fourni, étant donné que la signature des différentes méthodes")));
        assertEquals("e permet d'éviter que la chaîne de séparation ne soit interprétée comm",
                s.deserialize(s.serialize("e permet d'éviter que la chaîne de séparation ne soit interprétée comm")));

    }

    @Test
    void playerIdSerdeWorks(){
        PlayerId p = PLAYER_1;
        Serde<PlayerId> s = Serdes.PLAYER_ID_SERDE;
        assertEquals("0", s.serialize(p));
        assertEquals(p, s.deserialize(s.serialize(p)));
        assertEquals("1", s.serialize(p.next()));
        assertEquals(p.next(), s.deserialize(s.serialize(p.next())));
    }

    @Test
    void turnKindSerdeWorks(){
        Player.TurnKind k = Player.TurnKind.CLAIM_ROUTE;
        Serde<Player.TurnKind> s = Serdes.TURN_KIND_SERDE;
        assertEquals("2", s.serialize(k));
        assertEquals(k, s.deserialize(s.serialize(k)));
    }

    @Test
    void tradeTest(){
         SortedBag<Card> cardsReceive = SortedBag.of(BLACK);
         SortedBag<Card> cardsGive = SortedBag.of(RED);
         Ticket ticketsReceive = ChMap.tickets().get(0);
        Ticket ticketsGive = ChMap.tickets().get(1);
         Route routeReceive = ChMap.routes().get(0);
         Route routeGive = ChMap.routes().get(0);
        TradeDeal d = new TradeDeal(routeReceive, cardsReceive, ticketsReceive, routeGive, cardsGive, ticketsGive);
        TradeDeal d1 = Serdes.TRADE_DEAL_SERDE.deserialize(Serdes.TRADE_DEAL_SERDE.serialize(d));
        assertEquals(d1.cardsGive(), d.cardsGive());
        assertEquals(d1.routeGive(), d.routeGive());
    }

    @Test
    void chatMsgWorks(){
        ChatMessage msg = new ChatMessage("testing123", PLAYER_1);
        assertEquals("testing123", Serdes.CHAT_MESSAGE_SERDE.deserialize(Serdes.CHAT_MESSAGE_SERDE.serialize(msg)).message());
    }

    @Test
    void RouteSerdeWorks(){
        Route r = ChMap.routes().get(0);
        Serde<Route> s = Serdes.ROUTE_SERDE;
        assertEquals("0", s.serialize(r));
        assertEquals(r, s.deserialize(s.serialize(r)));
    }

    @Test
    void TicketSerdeWorks(){
        Ticket r = ChMap.tickets().get(0);
        Serde<Ticket> s = Serdes.TICKET_SERDE;
        assertEquals("0", s.serialize(r));
        assertEquals(r, s.deserialize(s.serialize(r)));
    }

    @Test
    void listString(){
        List<String> r = List.of("dsafsad", "test2");
        Serde<List<String>> s = Serdes.LIST_String_SERDE;
        System.out.println(s.serialize(r));
//        assertEquals("0", s.serialize(r));
        assertEquals(r, s.deserialize(s.serialize(r)));
    }

    @Test
    void cardString(){
        List<Card> r = List.of(Card.BLUE, Card.LOCOMOTIVE);
        Serde<List<Card>> s = Serdes.LIST_CARD_SERDE;
        System.out.println(s.serialize(r));
//        assertEquals("0", s.serialize(r));
        assertEquals(r, s.deserialize(s.serialize(r)));
    }

    @Test
    void sortedBagList(){
        List<SortedBag<Card>> r = List.of(SortedBag.of(Card.BLUE), SortedBag.of(2, Card.LOCOMOTIVE,3,  Card.BLACK));
        Serde<List<SortedBag<Card>>> s = Serdes.LIST_SORTED_BAG_CARD_SERDE;
        System.out.println(s.serialize(r));
//        assertEquals("0", s.serialize(r));
        assertEquals(r, s.deserialize(s.serialize(r)));
    }

    @Test
    void worksOnPlayerState(){
        PlayerState r = PlayerState.initial(SortedBag.of(2, Card.BLUE, 2, LOCOMOTIVE));
        Serde<PlayerState> s= Serdes.PLAYER_STATE_SERDE;
//        assertEquals(r, s.deserialize(s.serialize(r)));
        System.out.println(s.serialize(r));
        assert(r.cards().equals(s.deserialize(s.serialize(r)).cards()));

    }

    @Test
    void knownExampleWorks(){
        List<Card> fu = List.of(RED, WHITE, BLUE, BLACK, RED);
        PublicCardState cs = new PublicCardState(fu, 30, 31);
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PLAYER_1, new PublicPlayerState(10, 11, rs1),
                PLAYER_2, new PublicPlayerState(20, 21, List.of()));
        PublicGameState gs =
                new PublicGameState(40, cs, PLAYER_2, ps, null);
        Serde<PublicGameState> s = Serdes.PUBLIC_GAME_STATE_SERDE;
        assertEquals("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:", s.serialize(gs));
//        assertEquals(gs, s.deserialize(s.serialize(gs)));
        assertEquals(gs.currentPlayerState().carCount(),
                s.deserialize(s.serialize(gs)).currentPlayerState().carCount());
        assert(gs.claimedRoutes().equals(
                s.deserialize(s.serialize(gs)).claimedRoutes()));
        assert s.deserialize(s.serialize(gs)).lastPlayer() == null;
    }
}