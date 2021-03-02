package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import java.util.List;
import java.util.TreeSet;


/**
 * Ticket Class
 *
 * @author Eduardo Neville
 */
public final class Ticket implements Comparable<Ticket>{
       private final List<Trip> trips;
    
    
        public Ticket(List<Trip> trips){
        Preconditions.checkArgument(!trips.isEmpty());
        this.trips = trips;

    }

     public Ticket(Station from, Station to, int points) {

        this(List.of(new Trip(from,to,points)));

    }

    /**
     * Getter that gives us the Text of a ticket to later display
     * @param from Departing Station
     * @param to Arriving Station
     * @param points Points of the trip
     * @return Departing Station - to - Arriving Station (# of points)
     */
    
        public String text() {
        return computeText(trips);
    }
    
        /**
     * Compare the current Ticket with the given one
     * @param that The current ticket
     * @return Result if they are the same ticket or not
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
    
        public int compareTo(Ticket that) {
        return this.text().compareTo(that.text());
    }

    private static String computeText(List<Trip> trips){

        TreeSet<String> arrivals = new TreeSet<>();
        for (Trip trip : trips){
            arrivals.add(String.format("%s (%s)", trip.to().toString(), trip.points()));
        }
        //we add a delimiter so that we add la virgule
        String text = String.join(", ", arrivals);
        if (arrivals.size()>1){
            String finalText = "{ " + text + "}";
            return finalText;
        }
        return text;
    }
    
}

}
//Problem in the method points
