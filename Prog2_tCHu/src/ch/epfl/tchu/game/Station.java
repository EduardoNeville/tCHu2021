package ch.epfl.tchu.game;

import java.util.Objects;

import ch.epfl.tchu.Preconditions;

/**
 * Station Class
 *
 */
public final class Station {

    private final int id;
    private final String name;


    public Station(int id, String name){
        Preconditions.checkArgument(id >=0);
        this.id = id;
        this.name = name;

    }

    /**
     * Getter for the Station ID
     * @return ID
     */
    public int id(){
        return id;
    }

    /**
     * Getter for the Station Name
     * @return Name of Station
     */
    public String name(){
        return name;
    }



}
