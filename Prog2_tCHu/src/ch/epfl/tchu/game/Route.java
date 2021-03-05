package ch.epfl.tchu.game;


//Route Class

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ch.epfl.tchu.game.Constants.ADDITIONAL_TUNNEL_CARDS;

public final class Route {
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
     */
   public Route(String id, Station station1, Station station2, int length, Level level, Color color){
        Preconditions.checkArgument(station1.equals(station2) || (Constants.MIN_ROUTE_LENGTH <= length && length <= Constants.MAX_ROUTE_LENGTH));
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
        if(this.color == null){
            return null;
        }
        return color;
    }


    /**
     * @return List of the stations
     */
    public List<Station> stations(){
        List<Station> stations = new ArrayList<>();
        stations.add(station1);
        stations.add(station2);
            return stations;
    }

    //dns trail creer tu le mets en deuxieme arg addedtrail route.StationOpposite
    public Station stationOpposite(Station station){
        Preconditions.checkArgument(station.equals(station1) || station.equals(station2) );
        if (station.equals(station1)) {
            return station2;
        }
        else {
            return station1;
        }

    }

    public List<SortedBag<Card>> possibleClaimCards(){
        SortedBag<Card> possibleCards;
        List<SortedBag<Card>> listOfPossibleCards = List.of();

        if(level == Level.OVERGROUND){
            if(color == null){
                for(Color color : Color.ALL) {
                    possibleCards = SortedBag.of(length, Card.of(color));
                    listOfPossibleCards.add(possibleCards);
                }

            }
            else{
                possibleCards = SortedBag.of(length, Card.of(color));
                listOfPossibleCards.add(possibleCards);

            }
        }
        else {
            if(color == null){
                for(Color color : Color.ALL) {
                    for (int i = 0; i < length; i++) {
                        possibleCards = SortedBag.of((length - i), Card.of(color), i, Card.of(null));
                        listOfPossibleCards.add(possibleCards);
                    }
                }
                possibleCards = SortedBag.of(length, Card.of(null));
                listOfPossibleCards.add(possibleCards);
                }
            else{
                int i;
                for(i=0; i<length; i++){
                    possibleCards = SortedBag.of((length-i), Card.of(color), i, Card.of(null));
                    listOfPossibleCards.add(possibleCards);
                }
            }
        }
        return listOfPossibleCards;
    }

    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {

        Preconditions.checkArgument(level==Level.OVERGROUND && drawnCards.size() == ADDITIONAL_TUNNEL_CARDS);
        int counter = 0;
        for (Card card : drawnCards) {
            if (claimCards.contains(card) || card == Card.LOCOMOTIVE) {
                counter += 1;
            }
        }
        return counter;
    }

    public int claimPoints(){
            return Constants.ROUTE_CLAIM_POINTS.get(length);
        }

}
