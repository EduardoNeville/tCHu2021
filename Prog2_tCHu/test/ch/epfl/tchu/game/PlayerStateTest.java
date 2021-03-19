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
        assertEquals(0, ps.cards().size());
    }

    @Test
    void withAddedCard() {
        PlayerState ps = PlayerState.initial(SortedBag.of( 4, Card.ORANGE));
        assertEquals(4, ps.cards().size());
        ps = ps.withAddedCard(Card.LOCOMOTIVE);
        assertEquals(5, ps.cards());
    }

    @Test
    void withAddedCards() {
        PlayerState ps = PlayerState.initial(SortedBag.of( 4, Card.ORANGE));
        assertEquals(4, ps.cards().size());
        ps = ps.withAddedCards(SortedBag.of(2, Card.BLUE, 4, Card.LOCOMOTIVE));
        assertEquals(11, ps.cards());
    }

    @Test
    void canClaimRoute() {
        Route r = ChMap.routes().get(1);
        PlayerState ps = PlayerState.initial(SortedBag.of( 4, Card.RED));
        assertEquals(true, ps.canClaimRoute(r));
    }

    @Test
    void possibleClaimCards() {
    }

    @Test
    void possibleAdditionalCards() {
    }

    @Test
    void withClaimedRoute() {
        Route r = ChMap.routes().get(1);
        PlayerState ps = PlayerState.initial(SortedBag.of( 4, Card.BLUE));
        assertEquals(List.of(r), ps.withClaimedRoute(r, SortedBag.of(Card.RED)).routes());
    }

    @Test
    void ticketPoints() {
    }

    @Test
    void finalPoints() {
    }
}