package ch.epfl.tchu;
//https://cs108.epfl.ch

/**
 * Preconditions Class
 *
 * Class to check for conditions and throw exceptions
 * @author Eduardo Neville (314667)
 */
public final class Preconditions {
    private Preconditions(){};

    /**
     * Checks boolean, throws IAException if false
     * @param shouldBeTrue
     * @throws IllegalArgumentException
     */
    public static void checkArgument(boolean shouldBeTrue){
        if (!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }

    public static void checkIfEmpty(boolean shouldBeTrue){
        if (!shouldBeTrue){
            throw new NullPointerException();
        }
    }
}
