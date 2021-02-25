package ch.epfl.tchu.game;

/**
 * Station Connectivity  Interface
 *
 * @author Eduardo Neville
 */
public interface StationConnectivity {
    public abstract boolean connected(Station s1, Station s2);
}