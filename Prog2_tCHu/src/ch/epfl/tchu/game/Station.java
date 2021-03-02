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
        Preconditions.checkArgument(id >=0);
        this.id = id;
        this.name = name;

    }

    /**
     * Getter for the Station ID
     * @return ID
     */
    final int id(){
        return id;
    }

    /**
     * Getter for the Station Name
     * @return Name of Station
     */
    final String name(){
        return name;
    }



}
