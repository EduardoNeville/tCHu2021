package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import java.util.List;
import java.util.TreeSet;

/**
 * Ticket Class (implements Comparable<Ticket>)
 *
 * Constructors: Ticket
 * Getters: points
 * Methods: text, compareTo, and compareText
 *
 * @author Eduardo Neville (314667)
 * @author Martin Sanchez Lopez (313238) 
 */

public final class Ticket implements Comparable<Ticket>{
    private final List<Trip> trips;


    public Ticket(List<Trip> trips){
        Preconditions.checkArgument(!trips.isEmpty());
        this.trips = List.copyOf(trips); // Defensive copying

    }

    /**
     * Ticket Constructor
     * @param from Departing Station
     * @param to Arriving Station
     * @param points Points of the Ticket
     */
    public Ticket(Station from, Station to, int points) {

        this(List.of(new Trip(from,to,points)));

    }

    /**
     * Text method that returns the display Ticket
     * @return the text of the Ticket in a display fassion
     */
    public String text() {
        return computeText(trips);
    }

    /**
     * Getter for the # of points of the ticket connectivity
     * @param connectivity
     * @return Points of the trips connectivity
     */
    public final int points(StationConnectivity connectivity){
        int max= Integer.MIN_VALUE;
        for (Trip trips : trips){
            if (trips.points(connectivity)>max){
                max = trips.points(connectivity);
            }
        }
        return max;
    }

    /**
     * Compare the current Ticket with the given one
     * @param that The current ticket
     * @return Result if they are the same ticket or not
     */
    public int compareTo(Ticket that) {

        return this.text().compareTo(that.text());
    }

    /**
     * Forms the text ticket for a trip
     * @param trips The trips we are using
     * @return text of the ticket of a trip
     */
    private static String computeText(List<Trip> trips){

        TreeSet<String> arrivals = new TreeSet<>();
        for (Trip trip : trips){
            arrivals.add(String.format("%s (%s)", trip.to().name(), trip.points()));
        }
        //we add a delimiter so that we add la virgule
        String text = String.join(", ", arrivals);
        if (arrivals.size()>1){
            text = "{" + text + "}";
        }
        return trips.get(0).from().toString() + " - " + text;

    }
}
