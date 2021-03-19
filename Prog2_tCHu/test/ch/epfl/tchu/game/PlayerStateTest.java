package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerStateTest {

    @Test
    void initial() {
        SortedBag<Card> cardsForConstructor = SortedBag.of( 4, Card.ORANGE);
        SortedBag<Card> bagToChange = SortedBag.of( 4, Card.ORANGE);
        PlayerState ps = PlayerState.initial(bagToChange);
        bagToChange = SortedBag.of(4, Card.LOCOMOTIVE);
        assertEquals(cardsForConstructor, ps.cards());
    }

    @Test
    void initialThrowIAEonNotFourCards() {
        assertThrows(IllegalArgumentException.class, () -> {
            PlayerState.initial(SortedBag.of(2, Card.BLUE, 3, Card.ORANGE));
        });
    }

    @Test
    void tickets() {
        PlayerState ps = PlayerState.initial(SortedBag.of( 4, Card.ORANGE));
        assertEquals(0, ps.tickets().size());

    }

    @Test
    void withAddedTickets() {
        PlayerState ps = PlayerState.initial(SortedBag.of( 4, Card.ORANGE));
        assertEquals(0, ps.tickets().size());
        ps = ps.withAddedTickets(SortedBag.of(ChMap.tickets().get(0)));
        assertEquals(1, ps.tickets().size());
    }

    @Test
    void cards() {
        PlayerState ps = PlayerState.initial(SortedBag.of( 4, Card.ORANGE));
        assertEquals(4, ps.cards().size());
    }

    @Test
    void withAddedCard() {
        PlayerState ps = PlayerState.initial(SortedBag.of( 4, Card.ORANGE));
        assertEquals(4, ps.cards().size());
        ps = ps.withAddedCard(Card.LOCOMOTIVE);
        assertEquals(5, ps.cards().size());
    }

    @Test
    void withAddedCards() {
        PlayerState ps = PlayerState.initial(SortedBag.of( 4, Card.ORANGE));
        assertEquals(4, ps.cards().size());
        ps = ps.withAddedCards(SortedBag.of(2, Card.BLUE, 4, Card.LOCOMOTIVE));
        assertEquals(10, ps.cards().size());
    }

    @Test
    void canClaimRoute() {
        Route r = ChMap.routes().get(1);
        PlayerState ps = PlayerState.initial(SortedBag.of( 4, Card.RED));
        assertEquals(true, ps.canClaimRoute(r));
    }

    @Test
    void possibleClaimCards() {
        List<Route>  routes = ChMap.routes();
        PlayerState ps = PlayerState.initial(SortedBag.of( 3, Card.YELLOW, 1 , Card.LOCOMOTIVE));
        assertEquals(List.of(
                SortedBag.of(2, Card.YELLOW), SortedBag.of(1, Card.YELLOW, 1, Card.LOCOMOTIVE)),
                ps.possibleClaimCards(routes.get(6)));

        ps = ps.withAddedCards(SortedBag.of(3, Card.LOCOMOTIVE, 1, Card.YELLOW));
        ps = ps.withAddedCards(SortedBag.of(1, Card.LOCOMOTIVE, 1, Card.RED));


        assertEquals(List.of(
                SortedBag.of(1, Card.YELLOW, 1, Card.LOCOMOTIVE)),
                ps.possibleClaimCards(routes.get(1)));
    }

    @Test
    void possibleAdditionalCardsThrowsErrors() {
        Route r = ChMap.routes().get(1);
        PlayerState ps = PlayerState.initial(SortedBag.of( 4, Card.BLUE));
        SortedBag.Builder<Card> sB =new SortedBag.Builder<>();
        sB.add(SortedBag.of(2, Card.VIOLET, 1, Card.YELLOW)).add(Card.RED);

        assertThrows(IllegalArgumentException.class, () -> {
            ps.possibleAdditionalCards(0, SortedBag.of(2, Card.VIOLET, 1,
                    Card.YELLOW), SortedBag.of(2, Card.VIOLET, 1, Card.YELLOW));
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ps.possibleAdditionalCards(4, SortedBag.of(2, Card.VIOLET, 1, Card.YELLOW), SortedBag.of(2, Card.VIOLET, 1, Card.YELLOW));
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ps.possibleAdditionalCards(2, sB.build(), SortedBag.of(2, Card.VIOLET, 1, Card.YELLOW));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ps.possibleAdditionalCards(2, SortedBag.of(2, Card.VIOLET, 1, Card.YELLOW), SortedBag.of(2, Card.VIOLET, 3, Card.YELLOW));
        });

        ps.possibleAdditionalCards(2, SortedBag.of(1, Card.VIOLET, 1, Card.YELLOW), SortedBag.of(2, Card.VIOLET, 1, Card.YELLOW));

    }

    @Test
    void withClaimedRoute() {
        Route r = ChMap.routes().get(1);
        PlayerState ps = PlayerState.initial(SortedBag.of( 4, Card.BLUE));
        assertEquals(List.of(r), ps.withClaimedRoute(r, SortedBag.of(Card.RED)).routes());
    }

    @Test
    void ticketPoints() {
        List<Route>  routes = ChMap.routes();
        PlayerState ps = PlayerState.initial(SortedBag.of( 4, Card.ORANGE));
        assertEquals(0, ps.ticketPoints());
        ps = ps.withAddedTickets(SortedBag.of(ChMap.tickets().get(1)));
        assertEquals(-10, ps.ticketPoints());
        ps = ps.withAddedTickets(SortedBag.of(ChMap.tickets().get(0)));
        ps = ps.withClaimedRoute(routes.get(7), SortedBag.of()).withClaimedRoute(routes.get(67), SortedBag.of())
                .withClaimedRoute(routes.get(19), SortedBag.of());
        assertEquals(-5, ps.ticketPoints());

    }

    @Test
    void finalPoints() {
    }
}
