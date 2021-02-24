package ch.epfl.tchu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PreconditionsTest {

    private
    @Test
    void checkArgumentSucceedsForTrue() {
        Preconditions.checkArgument(true);
    }

    @Test
    void checkArgumentFailsForFalse() {
        assertThrows(IllegalArgumentException.class, () -> {
            Preconditions.checkArgument(false);
        });
    }
}
