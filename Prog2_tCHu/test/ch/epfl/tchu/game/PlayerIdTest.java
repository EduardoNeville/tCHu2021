package ch.epfl.tchu.game;

import ch.epfl.tchu.game.PlayerId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerIdTest {

    @Test
    void nextWorks() {
        PlayerId p= PlayerId.PLAYER_1;
        assertEquals(PlayerId.PLAYER_2, p.next());
        assertEquals(PlayerId.PLAYER_2, p.next());
        p = p.next();
        assertEquals(PlayerId.PLAYER_1, p.next());
    }

    @Test
    void countCorrect() {
        assertEquals(2, PlayerId.COUNT);
    }

    @Test
    void allIsCorrect() {
        assertEquals(List.of(PlayerId.PLAYER_1, PlayerId.PLAYER_2), PlayerId.ALL);
    }
}