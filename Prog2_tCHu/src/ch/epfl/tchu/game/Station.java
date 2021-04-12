package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Objects;

/**
 *  Station Class
 *
 *  Constructors: Station
 *  Getters: id, name, toString
 *
 *  @author Eduardo Neville (314667)
 */
public final class Station {

    private final int id;
    private final String name;


    /**
     * Contruct a Station with an id and a name
     * @param id
     *          Id of the Station
     * @param name
     *          Name of the Station
     * @throws IllegalArgumentException
     *          if id not higher that or equal to 0
     */
    public Station(int id, String name){
        Preconditions.checkArgument(id >=0);
        this.id = id;
        this.name = Objects.requireNonNull(name); ;
    }

    /**
     * Getter for the id of the Station
     * @return Id of the Station
     */
    public int id(){
        return id;
    }

    /**
     * Getter for the id of the Station
     * @return Name of the Station
     */
    public String name(){ return name; }

    /**
     * toString for the Station name
     * @return Name of the Station
     */
    public String toString(){
        return name;
    }
}
