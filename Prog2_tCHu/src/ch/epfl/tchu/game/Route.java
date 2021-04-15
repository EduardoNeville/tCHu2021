package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ch.epfl.tchu.game.Constants.MAX_ROUTE_LENGTH;
import static ch.epfl.tchu.game.Constants.MIN_ROUTE_LENGTH;


/**
 * Route Class
 * Class that allows us to represent a road connecting two neighboring towns
 *
 * @author Martin Sanchez Lopez (313238)
 */
public final class Route {


    /**
     * A Route level (elevation).
     */
    public enum Level {
        OVERGROUND, UNDERGROUND
    }

    private final String id;
    private final Station station1, station2;
    private final int length;
    private final Level level;
    private final Color color;

    /**
     * Constructs a Route with and id, 2 different stations, a length, a level and a color (or none).
     *
     * @param id       id of the route
     * @param station1 station 1
     * @param station2 station 2
     * @param length   length of the route
     * @param level    level of the route
     * @param color    color of the route
     *                 can be null
     * @throws IllegalArgumentException
     *                  if station1 is equal to station2
     * @throws IllegalArgumentException
     *                  if length is smaller than MIN_ROUTE_LENGTH
     *                  or length is bigger than MAX_ROUTE_LENGTH
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(!station1.equals(station2));
        Preconditions.checkArgument(length >= MIN_ROUTE_LENGTH && length <= MAX_ROUTE_LENGTH);

        this.id = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.level = Objects.requireNonNull(level);
        this.length = length;
        this.color = color;
    }

    /**
     * Returns the id of the route.
     * @return the id of the route
     */
    public String id() {
        return id;
    }

    /**
     * Returns the station 1 of this route.
     * @return the station 1 of this route
     */
    public Station station1() {
        return station1;
    }

    /**
     * Returns the station 2 of this route.
     * @return the station 2 of this route
     */
    public Station station2() {
        return station2;
    }

    /**
     * Returns the a list with both stations of this route.
     * @return the a list with both stations of this route
     */
    public List<Station> stations() {
        return List.of(station1, station2);
    }

    /**
     * Returns the opposite station of this route to the station given.
     *
     * @param station station opposite of the station to be returned
     * @return the opposite station of this route to the station given
     * @throws IllegalArgumentException if the station given is not one of the stations of this Route
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station.equals(station1) || station.equals(station2));

        return (station == station1) ? station2 : station1;
    }

    /**
     * Returns a list of card combinations that are possible to play to claim this route.
     * @return a list of card combinations that are possible to play to claim this route
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> cardCombinations = new ArrayList<>();
        List<Color> colors;
        boolean isUnderground = level == Level.UNDERGROUND;
        if (color == null) {
            colors = Color.ALL;
        } else {
            colors = List.of(color);
        }
        if (isUnderground) {
            for (int i = 0; i < length; i++) {
                for (Color c : colors) {
                    cardCombinations.add(SortedBag.of(length - i, Card.of(c), i, Card.LOCOMOTIVE));
                }
            }
            //add all locomotive combination at end
            cardCombinations.add(SortedBag.of(length, Card.LOCOMOTIVE));
        }
        if (!isUnderground) {
            for (Color c : colors) {
                cardCombinations.add(SortedBag.of(length, Card.of(c)));
            }
        }

        return cardCombinations;
    }

    /**
     * Returns the number of additional card a players has to play to claim an underground road.
     *
     * @param claimCards cards the player played to initiate the claim on road
     * @param drawnCards cards the player drew after playing his cards to claim the road
     * @return the number of additional card a players has to play to claim an underground road
     * @throws IllegalArgumentException if this road is not UNDERGROUND
     *                                  if the number of <code>drawnCards</code> is not equal to the number of cards to draw
     *                                  for tunnel acquisition by <code>Constant</code>
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        int nbOfCards = drawnCards.size();

        Preconditions.checkArgument(level == Level.UNDERGROUND);
        Preconditions.checkArgument(nbOfCards == Constants.ADDITIONAL_TUNNEL_CARDS);


        int cardCount = 0;
        boolean hasLocomotive = false;
        ArrayList<Color> colors = new ArrayList<>();
        if (claimCards.contains(Card.LOCOMOTIVE)) {
            hasLocomotive = true;
        }
        //add colors that players played
        for (Card c : claimCards) {
            if (!c.equals(Card.LOCOMOTIVE) && !colors.contains(c.color())) {
                colors.add(c.color());
            }
        }
        //counts additional cards to play
        for (Card card : drawnCards) {
            if ((hasLocomotive && colors.isEmpty() && card.equals(Card.LOCOMOTIVE))
                    || colors.contains(card.color())
                    || card.equals(Card.LOCOMOTIVE)) {
                cardCount += 1;
            }
        }
        return cardCount;
    }

    /**
     * Returns the number of points that the construction of this road rewards.
     * @return the number of points that the construction of this road rewards
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }

    /**
     * Returns the length of this Route.
     * @return the length of this Route
     */
    public int length() {
        return length;
    }

    /**
     * Retuns the level of this Route.
     * @return the level of this Route
     */
    public Level level() {
        return level;
    }

    /**
     * Returns the color of this Route.
     * @return the color of this Route
     */
    public Color color() {
        return color;
    }

}
