package ch.epfl.tchu.game;


/**
 * Route Class: Class that allows us to represent a road connecting two neighboring towns
 *
 * @author Hamza Karime
 */

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ch.epfl.tchu.game.Constants.ADDITIONAL_TUNNEL_CARDS;

/**
 * An enumerated type representing the two levels at which a road can be found. Its values are, in order
 */
public final class Route {

    /**
     * An enumerated type representing the two levels at which a road can be found. Its values are, in order
     */

    public enum Level{
        OVERGROUND, //Route en surface
        UNDERGROUND // Route en tunnel
    }

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;


    /**
     * @param id
     * @param station1
     * @param station2
     * @param length
     * @param level
     * @param color
     * who builds a road with the given identity, stations, length, level and color;
     * throws IllegalArgumentException if both stations are equal or if the length is not within acceptable limits;
     * or NullPointerException if the identity, one of the two stations or the level are null. Note that the color can however be zero,
     * which means the road is neutral in color.
     */
   public Route(String id, Station station1, Station station2, int length, Level level, Color color){
       Preconditions.checkArgument(!station1.equals(station2));
       Preconditions.checkArgument((Constants.MIN_ROUTE_LENGTH <= length && length <= Constants.MAX_ROUTE_LENGTH));
        this.id = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.length = length;
        this.level = Objects.requireNonNull(level);
        this.color = color;
    }

    /**
     * Getter for ID of the route
     * @return ID of route
     */
    public String id() {
        return id;
    }

    /**
     * Getter for the 1st Station of the route
     * @return 1st Station of the route
     */
    public Station station1() {
        return station1;
    }

    /**
     * Getter for the Last Station of the route
     * @return Last Station of the route
     */
    public Station station2() {
        return station2;
    }

    /**
     * Getter for the length of a route
     * @return The length of the route
     */
    public int length() {
        return length;
    }

    /**
     * Getter for the level of a route
     * @return The level of the route
     */
    public Level level(){ return level; }

    /**
     * Getter for the color of a route
     * @return Color of the route
     */

    public Color color() {
        return color;
    }

    /**
     * @return the list of the two stations of the route, in the order
     * in which they were passed to the constructor
     */
    public List<Station> stations(){
        return List.of(station1, station2);
    }

    //dns trail creer tu le mets en deuxieme arg addedtrail route.StationOpposite


    /**
     * @param station
     * @return the station of the route which is not the given one, or throws IllegalArgumentException
     * if the given station is neither the first nor the second station of the route
     */
    public Station stationOpposite(Station station){
        Preconditions.checkArgument(station.equals(station1) || station.equals(station2) );
        if (station.equals(station1)) {
            return station2;
        }
        else {
            return station1;
        }

    }

    /**
     * @return which returns the list of all the sets of cards that could be played to (attempt to)
     * seize the road, sorted in ascending order of number of locomotive cards, then by color
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> possibleCards = new ArrayList<>();

        if (level == Level.OVERGROUND) {
            if (color == null) {
                for (Card card : Card.CARS) {
                    possibleCards.add(SortedBag.of(length, card));
                }
            } else {
                possibleCards = List.of(SortedBag.of(length, Card.of(color)));
            }
        } else {
            if (color == null) {
                for (int i = 0; i < length; ++i) {
                    for (Card card : Card.CARS) {
                        possibleCards.add(SortedBag.of(length - i, card, i, Card.LOCOMOTIVE));
                    }
                }
                possibleCards.add(SortedBag.of(length, Card.LOCOMOTIVE));
            } else {
                possibleCards.add(SortedBag.of(length, Card.of(color)));
                for (int i = 1; i <= length; ++i) {
                    possibleCards.add(SortedBag.of(length - i, Card.of(color), i, Card.LOCOMOTIVE));
                }
            }
        }
        return possibleCards;
    }

    /**
     * @param claimCards
     * @param drawnCards
     * @return which returns the number of additional cards to be played to seize the road (in tunnel),
     * knowing that the player has initially put down the claimCards and that the three cards drawn from the top of the pile are drawnCards;
     * throws the IllegalArgumentException if the route to which it is applied is not a tunnel,
     * or if drawnCards does not contain exactly 3 cards2,
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {


        Preconditions.checkArgument(!(level==Level.OVERGROUND && drawnCards.size() == ADDITIONAL_TUNNEL_CARDS));
        int counter = 0;
        for (Card card : drawnCards) {
            if (claimCards.contains(card) || card == Card.LOCOMOTIVE) {
                counter += 1;
            }
        }
        return counter;
    }

    /**
     * @return the number of construction points a player gets when they grab the road.
     */
    public int claimPoints(){
            return Constants.ROUTE_CLAIM_POINTS.get(length);
        }

}
