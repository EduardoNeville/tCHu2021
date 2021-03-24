package ch.epfl.tchu.game;

/**
 * Station Connectivity  Interface
 *
 * @author Eduardo Neville (314667)
 * @author Martin Sanchez Lopez (313238)
 */
public interface StationConnectivity {
    public abstract boolean connected(Station s1, Station s2);
}
