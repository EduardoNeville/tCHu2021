package ch.epfl.tchu.game;

import java.util.List;

/**
 * A player id.
 *
 * @author Eduardo Neville (314667)
 * @author Martin Sanchez Lopez (313238)
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2;

    public static final List<PlayerId> ALL = List.of(values());

    public static final int COUNT = ALL.size();

    /**
     * Returns the opposite id from this one.
     * @return the opposite id from this one
     */
    public PlayerId next(){
        return (this.equals(PLAYER_1)) ? PLAYER_2 : PLAYER_1;
    }


}
