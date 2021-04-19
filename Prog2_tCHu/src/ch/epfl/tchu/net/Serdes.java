package ch.epfl.tchu.net;

public class Serdes {
    public static final Serde<Integer> INTEGER = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt);
}
