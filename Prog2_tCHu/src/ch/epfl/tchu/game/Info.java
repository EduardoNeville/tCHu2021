package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import static ch.epfl.tchu.gui.StringsFr.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Info class allows you to generate the texts describing the progress of the game
 */
public final class Info {

    private final String playerName;

    /**
     * @param playerName
     * Unfo class constructor
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }


    /**
     * @param card
     * @return the card color name
     */
    private static String colorName(Card card){
        //String colorName;
        switch(card){
            case BLACK: return BLACK_CARD;
            case VIOLET:return VIOLET_CARD;
            case BLUE: return BLUE_CARD;
            case GREEN: return GREEN_CARD;
            case YELLOW: return YELLOW_CARD;
            case ORANGE: return ORANGE_CARD;
            case RED: return RED_CARD;
            case WHITE: return WHITE_CARD;
            default: return LOCOMOTIVE_CARD;
        }
    }

    /**
     * @param route
     * @return the two cities of the route separated with a en dash
     */
    private static String routeName(Route route){
        return route.station1().toString()+EN_DASH_SEPARATOR+route.station2().toString();
    }

    /**
     * @param cards
     * @return the name of the card with the and separator
     */
    private static String cardToString(SortedBag<Card> cards) {
        List<String> carteToSet = new ArrayList<>();
        String carteToSetString = ("");
        for (Card c : cards.toSet()) {
            int n = cards.countOf(c);
            String s = (n + " " + cardName(c, n));
            carteToSet.add(s);
        }
        switch(carteToSet.size()){
            case 1: carteToSetString = carteToSet.get(0);
            break;
            case 2: carteToSetString = String.join(AND_SEPARATOR, carteToSet);
            break;
            default: String lastString = carteToSet.get(carteToSet.size() -1);
            carteToSet.remove(carteToSet.size()-1);
            carteToSetString = String.join(", ", carteToSet) + AND_SEPARATOR + lastString;
        }
        return carteToSetString;
    }

    /**
     * @param card
     * @param count
     * @return the name of the given card
     */
    public static String cardName(Card card, int count){
        return String.format("%s", colorName(card)) + plural(count);
    }

    /**
     * @param playerNames
     * @param points
     * @return the message stating that the players
     */
    public static String draw(List<String> playerNames, int points){
        return String.format(DRAW, String.join(AND_SEPARATOR, playerNames), points);
    }

    /**
     * @return the message stating that the player will play first
     */
    public String willPlayFirst(){
        return String.format(WILL_PLAY_FIRST, playerName);
    }

    /**
     * @param count
     * @return the message declaring that the player has kept the given number of tickets
     */
    public String keptTickets(int count){
        return String.format(KEPT_N_TICKETS, playerName, count, plural(count));
    }

    /**
     * @return the message declaring that the player can play
     */
    public String canPlay(){
        return String.format(CAN_PLAY, playerName);
    }

    /**
     * @param count
     * @return the message stating that the player has drawn the given number of tickets
     */
    public String drewTickets(int count){
        return String.format(DREW_TICKETS, playerName, count, plural(count));
    }

    /**
     * @return the message declaring that the player has drawn a card "blind", ie from the top of the draw pile
     */
    public String drewBlindCard(){
        return String.format(DREW_BLIND_CARD, playerName);
    }

    /**
     * @param card the message declaring that the player has drawn the face-up card given
     * @return
     */
    public String drewVisibleCard(Card card){
        return String.format(DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     * @param route
     * @param cards
     * @return the message stating that the player has seized the given route using the given cards
     */
    public String claimedRoute(Route route, SortedBag<Card> cards){
        return String.format(CLAIMED_ROUTE, playerName, routeName(route), cardToString(cards)); //A VERIFIER cards
    }

    /**
     * @param route
     * @param initialCards
     * @return the message stating that the player wishes to seize the given tunnel route using initially the given cards
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards){
        return String.format(ATTEMPTS_TUNNEL_CLAIM, playerName, routeName(route), cardToString(initialCards)); // A VERIFIER inCARds
    }

    /**
     * @param drawnCards
     * @param additionalCost
     * @return the message stating that the player has drawn the three additional cards given, and that they involve an additional cost of the number of cards given
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        if (additionalCost == 0) {
            return String.format(ADDITIONAL_CARDS_ARE, cardToString(drawnCards)) + String.format(NO_ADDITIONAL_COST, plural(additionalCost));
        } else
            return String.format(ADDITIONAL_CARDS_ARE, cardToString(drawnCards)) + String.format(SOME_ADDITIONAL_COST, additionalCost, plural(additionalCost));
    }

    /**
     * @param route
     * @return the message stating that the player could not (or wanted) to seize the given tunnel
     */
    public String didNotClaimRoute(Route route){
        return String.format(DID_NOT_CLAIM_ROUTE, playerName, routeName(route));
    }

    /**
     * @param carCount
     * @return the message declaring that the player has only the given number (and less than or equal to 2) of wagons, and that the last turn therefore begins
     */
    public String lastTurnBegins(int carCount){
        return  String.format(LAST_TURN_BEGINS, playerName, carCount, plural(carCount));
    }

    /**
     * @param longestTrail
     * @return the message declaring that the player obtains the end-of-game bonus thanks to the given path, which is the longest, or one of the longest
     */
    public String getsLongestTrailBonus(Trail longestTrail){
        String trail = longestTrail.station1()+EN_DASH_SEPARATOR+longestTrail.station2();
        return String.format(GETS_BONUS, playerName, trail); // A VERIFIER
    }

    /**
     * @param points
     * @param loserPoints
     * @return which returns the message declaring that the player wins the game with the number of points given, his opponent having only obtained loserPoints
     */
    public String won(int points, int loserPoints){
        return String.format(WINS, playerName, points, plural(points), loserPoints, plural(loserPoints));
    }

}
