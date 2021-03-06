package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Objects;

/**
 *  Station Class
 *
 *  Constructors: Station
 *  Getters: id, name, toString
 *
 *  @author Eduardo Neville
 *  @author Hamza Karime
 */
public final class Station {

    private final int id;
    private final String name;


    public Station(int id, String name){
        Preconditions.checkArgument(id >=0);
        this.id = id;
        this.name = Objects.requireNonNull(name); ;

    }

    public int id(){
        return id;
    }

    public String name(){ return name; }

    public String toString(){
        return name;
    }


}
