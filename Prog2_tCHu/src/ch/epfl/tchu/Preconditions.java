package ch.epfl.tchu;

/**
 * Preconditions Class
 * <p>
 * Class to check for conditions and throw exceptions
 *
 * @author Eduardo Neville (314667)
 */
public final class Preconditions {
    private Preconditions() {
    }

    /**
     * Checks boolean, throws IAException if false
     *
     * @param shouldBeTrue argument that should be true
     * @throws IllegalArgumentException if the given parameter is not true
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
