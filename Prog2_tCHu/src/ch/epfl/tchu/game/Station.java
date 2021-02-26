package ch.epfl.tchu.game;

/**
 * Station Class
 *
 * @author Eduardo Neville
 */
public final class Station {

    private final int id;
    private final String name;


    public Station(int id, String name){
        this.id = id;
        this.name = name;
        // throw IllegalArgumentException

    }

    /**
     * Getter for the Station ID
     * @return ID
     */
    final int GetId(){
        return id;
    }

    /**
     * Getter for the Station Name
     * @return Name of Station
     */
    final String GetName(){
        return name;
    }

    /** To fix
        final String toString(String name, int id){
        return String.format(name + id);
    }
     */



}
