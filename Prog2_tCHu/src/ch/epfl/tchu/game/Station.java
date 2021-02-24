package ch.epfl.tchu.game;

public final class Station {

    private final int id;
    private final String name;

    public Station(int id, String name){
        this.id = id;
        this.name = name;
/**        boolean BooleanID;
 * if (id<0){
 *BooleanID = false;
 Preconditions.checkArgument(BooleanID);
 }
 */
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
     * @return
     */
    final String name(){
        return name;
    }

    /** To fix
        final String toString(String name, int id){
        return String.format(name + id);
    }
     */



}
