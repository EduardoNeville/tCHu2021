package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

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
        ps = ps.withAddedTickets(SortedBag.of(ChMap.tickets().get(0)));
        assertEquals(1, ps.tickets().size());

    }

    @Test
    void withAddedTickets() {
    }

    @Test
    void cards() {
    }

    @Test
    void withAddedCard() {
    }

    @Test
    void withAddedCards() {
    }

    @Test
    void canClaimRoute() {
    }

    @Test
    void possibleClaimCards() {
    }

    @Test
    void possibleAdditionalCards() {
    }

    @Test
    void withClaimedRoute() {
    }

    @Test
    void ticketPoints() {
    }

    @Test
    void finalPoints() {
    }
}