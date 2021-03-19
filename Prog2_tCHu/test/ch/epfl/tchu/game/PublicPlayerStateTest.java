package ch.epfl.tchu.game;

import ch.epfl.tchu.game.PublicPlayerState;
import ch.epfl.tchu.game.Route;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PublicPlayerStateTest {
    static PublicPlayerState publicPlayerState(int tickets, int cards){
        //int tC= 2;
        //int cC = 3;
        List<Route> routes = List.of(ChMap.routes().get(0), ChMap.routes().get(1), ChMap.routes().get(2));

        PublicPlayerState pps = new PublicPlayerState(tickets, cards, routes);


        return pps;

    }

    @Test
    void ticketCount() {
        int t0 = 4;
        int t1 = 5456;
        int t2 = 0;

        //PublicPlayerState publicPlayerState = publicPlayerState(t0, 3);
        assertEquals(t0, publicPlayerState(t0, 3).ticketCount());
        assertEquals(t1, publicPlayerState(t1, 3).ticketCount());

        assertEquals(t2, publicPlayerState(t2, 3).ticketCount());

    }

    @Test
    void ticketCountThrowsIAEonNegativeCount() {
        int t0 = 4;
        int tN = -3;
        int t1 = 5456;
        int t2 = 0;

        List<Route> routes = List.of(ChMap.routes().get(0), ChMap.routes().get(1), ChMap.routes().get(2));


        //PublicPlayerState publicPlayerState = publicPlayerState(t0, 3);
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicPlayerState(tN, t0, routes);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new PublicPlayerState(t1, tN, routes);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new PublicPlayerState(tN, tN, routes);
        });

    }

    @Test
    void cardCount() {
        int t0 = 4;
        int t1 = 5456;
        int t2 = 0;

        //PublicPlayerState publicPlayerState = publicPlayerState(t0, 3);
        assertEquals(t0, publicPlayerState(1, t0).cardCount());
        assertEquals(t1, publicPlayerState(1, t1).cardCount());

        assertEquals(t2, publicPlayerState(1, t2).cardCount());

    }

    @Test
    void routes() {
        List<Route> unchangedRoutes = List.of(ChMap.routes().get(0), ChMap.routes().get(1), ChMap.routes().get(2));
        List<Route> routes;

        routes = List.of(ChMap.routes().get(0), ChMap.routes().get(1), ChMap.routes().get(2));

        PublicPlayerState publicPlayerState = new PublicPlayerState(2, 2, routes);

        assertEquals(unchangedRoutes, publicPlayerState.routes());

        routes =  List.of(ChMap.routes().get(2), ChMap.routes().get(4), ChMap.routes().get(5));
        assertEquals(unchangedRoutes, publicPlayerState.routes());

    }

    @Test
    void carCount() {
        assertEquals(32, publicPlayerState(2,2).carCount());

    }

    @Test
    void claimPoints() {
        int points = 0;
        points += Constants.ROUTE_CLAIM_POINTS.get(4);
        points += Constants.ROUTE_CLAIM_POINTS.get(1);
        points += Constants.ROUTE_CLAIM_POINTS.get(3);

        assertEquals(points, publicPlayerState(2,2).claimPoints());
    }

}