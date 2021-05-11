package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * A serde that serves to serialize and deserialize an object to a string, and from a string respectively.
 *
 * @param <E> type of object of the serde
 * @author Martin Sanchez Lopez (313238)
 */
public interface Serde<E> {

    /**
     * Serializes the given object to a string.
     *
     * @param object object to serialize
     * @return a string of the serialization of the given object
     */
    String serialize(E object);

    /**
     * Serializes the given string to an object of the serde's type.
     *
     * @param string serialization to decode
     * @return the object that was decoded as a result of the deserialization of the given string
     */
    E deserialize(String string);

    /**
     * Returns an anonymous instantiation of a serde with the given methods.
     *
     * @param serialization   the serialization method
     * @param deserialization the deserialization method
     * @param <T>             the serde's type
     * @return an anonymous instantiation of a serde with the given methods
     */
    static <T> Serde<T> of(Function<T, String> serialization, Function<String, T> deserialization) {
        return new Serde<>() {
            @Override
            public String serialize(T object) {
                return serialization.apply(object);
            }

            @Override
            public T deserialize(String string) {
                return deserialization.apply(string);
            }
        };
    }

    /**
     * Returns an anonymous instantiation of a serde with the implemented methods to (de)serialize elements of a
     * list of possible values.
     *
     * @param list the list with all the possible values
     * @param <T>  type of the serde to be returned
     * @return an anonymous instantiation of a serde with the implemented methods for lists TODO:change?
     */
    static <T> Serde<T> oneOf(List<T> list) {
        return new Serde<>() {
            @Override
            public String serialize(T object) {
                return Integer.toString(list.indexOf(object));
            }

            @Override
            public T deserialize(String string) {
                return list.get(Integer.parseInt(string));
            }
        };
    }

    /**
     * Returns an anonymous instantiation of a serde with the implemented methods to (de)serialize lists of elements
     * with a given serde and separator character.
     *
     * @param serde serde with the wanted (de)serialization of the individual elements
     * @param c     separation character in the serialized string
     * @param <T>   type of the returned serde
     * @return an anonymous instantiation of a serde to (de)serialize lists of objects
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde, String c) {
        return new Serde<>() {

            @Override
            public String serialize(List<T> object) {
                List<String> list = new ArrayList<>();
                object.forEach(o -> list.add(serde.serialize(o)));
                return String.join(c, list);
            }

            @Override
            public List<T> deserialize(String string) {
                List<T> deserializedList = new ArrayList<>();
                String[] stringArray = string.split(Pattern.quote(c), -1);
                Arrays.stream(stringArray).forEach(s -> deserializedList.add(serde.deserialize(s)));

                return deserializedList;
            }
        };
    }

    /**
     * Returns an anonymous instantiation of a serde with the implemented methods to (de)serialize bags of elements
     * with a given serde and separator character.
     *
     * @param serde serde with the wanted (de)serialization of the individual elements
     * @param c     separation character in the serialized string
     * @param <T>   type of the returned serde
     * @return an anonymous instantiation of a serde to (de)serialize bags of objects
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String c) {
        return new Serde<>() {
            @Override
            public String serialize(SortedBag<T> object) {
                List<String> list = new ArrayList<>();
                object.forEach(o -> list.add(serde.serialize(o)));
                return String.join(c, list);
            }

            @Override
            public SortedBag<T> deserialize(String string) {
                List<T> deserializedList = new ArrayList<>();
                String[] stringArray = string.split(Pattern.quote(c), -1);
                Arrays.stream(stringArray).forEach(s -> deserializedList.add(serde.deserialize(s)));

                return SortedBag.of(deserializedList);
            }
        };
    }

}