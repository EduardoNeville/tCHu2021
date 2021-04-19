package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.*;
import java.util.function.Function;

public interface Serde<E> {

    public String serialize(E object);
    public E deserialize(String string);

    public static <T> Serde<T> of(Function<T, String> serialization, Function<String, T> deserialization){
        return new Serde<T>() {
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

    public static <T> Serde<T> oneOf(List<T> list){
        return new Serde<T>() {
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

    public static <T> Serde<List<T>> listOf(Serde<T> serde, String c){
        return new Serde<List<T>>() {

            @Override
            public String serialize(List<T> object) {
                List<String> list = new ArrayList<>();
                object.forEach(o -> {
                   list.add(serde.serialize(o));
                });
                String s = String.join(c, list);
                return s;
            }

            @Override
            public List<T> deserialize(String string) {
                return null; //TODO
            }
        };
    }


    public static <T> Serde<SortedBag<T>> bagOf(Serde<T> serdeList, String c){
        return new Serde<SortedBag<T>>() {
            @Override
            public String serialize(SortedBag<T> object) {
                return null;
            }

            @Override
            public SortedBag<T> deserialize(String string) {
                return null;
            }
        }
    }




}
