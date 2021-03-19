package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.tchu.gui.StringsFr.*;

/**
 * Information texts about the state of the game and player actions for the GUI.
 *
 * @author Martin Sanchez Lopez (313238)
 */
public final class Info {

    private final String playerName;

    /**
     * Constructs Information instance associated with a player.
     * @param playerName
     *          name of the player
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Returns a string of the card name in french.
     * @param card
     *          card to translate
     * @param count
     *          amount of the card (for plural/singular)
     * @return a string of the card name in french
     */
    public static String cardName(Card card, int count){
        StringBuilder name = new StringBuilder();

        if (card.equals(Card.LOCOMOTIVE)){
            name.append(LOCOMOTIVE_CARD);
        }
        else {
            switch (card.color()) { //TODO: breaks useless ?
                case RED:
                    name.append(RED_CARD);
                    break;
                case BLUE:
                    name.append(BLUE_CARD);
                    break;
                case BLACK:
                    name.append(BLACK_CARD);
                    break;
                case GREEN:
                    name.append(GREEN_CARD);
                    break;
                case ORANGE:
                    name.append(ORANGE_CARD);
                    break;
                case VIOLET:
                    name.append(VIOLET_CARD);
                    break;
                case WHITE:
                    name.append(WHITE_CARD);
                    break;
                case YELLOW:
                    name.append(YELLOW_CARD);
                    break;
                default:
                    throw new IllegalArgumentException(); //as per TA recommendation
            }
        }

        // appends the s if plural and builds the string
        return name.append(plural(count)).toString();
    }

    /**
     * Returns a draw message of the players that draw and their points.
     *
     * @param playerNames
     *         players in a draw
     * @param points
     *          amount of points in which they draw
     * @return a draw message of the players that draw and their points
     *
     * TODO: retarded method, til tchu is 2 player game
     */
    public static String draw(List<String> playerNames, int points){
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> playerNamesArray = new ArrayList<>();
        playerNamesArray.addAll(playerNames);

        stringBuilder.append(String.join(", ", playerNamesArray.subList(0, playerNamesArray.size()-1)));
        stringBuilder.append(AND_SEPARATOR);

        String drawString = String.format(DRAW
                ,stringBuilder.append(playerNamesArray.get(playerNamesArray.size()-1)).toString()
                ,points
                , plural(points));

        return drawString;
    }

    /**
     * Returns a message saying that this instance's player will play first.
     * @return a message saying that this instance's player will play first
     */
    public String willPlayFirst(){
        return String.format(WILL_PLAY_FIRST, playerName);
    }

    /**
     * Returns a message saying that this instance's player will keep his tickets.
     * @param count
     *          amount of ticket kept
     * @return a message saying that this instance's player will keep his tickets
     */
    public String keptTickets(int count){
        return String.format(KEPT_N_TICKETS, playerName, count, plural(count));
    }

    /**
     * Returns a message saying that this instance's player can play
     * @return a message saying that this instance's player can play
     */
    public String canPlay(){
        return String.format(CAN_PLAY, playerName);
    }

    /**
     * Returns a message saying that this instance's player drew <code>count</code> tickets.
     * @param count
     *          amount of tickets drawn
     * @return a message saying that this instance's player drew tickets
     */
    public String drewTickets(int count){
        return String.format(DREW_TICKETS, playerName, count, plural(count));
    }

    /**
     * Returns a message saying that this instance's player drew a blind card.
     * @return a message saying that this instance's player drew a blind card
     */
    public String drewBlindCard(){
        return String.format(DREW_BLIND_CARD, playerName);
    }

    /**
     * Returns a message saying that this instance's player drew a visible card.
     * @param card
     *          card drawn
     * @return a message saying that this instance's player drew a visible card
     */
    public String drewVisibleCard(Card card){
        return String.format(DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     * Returns a message saying that this instance's player claimed a route.
     * @param route
     *          route claimed
     * @param cards
     *          cards used to claim
     * @return a message that this instance's player claimed a route
     */
    public String claimedRoute(Route route, SortedBag<Card> cards){
        return String.format(CLAIMED_ROUTE, playerName, routeString(route), cardsString(cards));
    }

    /**
     * Returns a message saying that this instance's player attempt to claim a tunnel.
     * @param route
     *          route attempted to be claimed
     * @param initialCards
     *          cards used to claim
     * @return a message saying that this instance's player attempt to claim a tunnel
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards){
        return String.format(ATTEMPTS_TUNNEL_CLAIM, playerName, routeString(route), cardsString(initialCards));
    }

    /**
     * Returns a message saying that this instance's player drew additional cards and the additional cost they demand.
     * @param drawnCards
     *          cards draw
     * @param additionalCost
     *          additional cost
     * @return a message saying that this instance's player drew additional cards and the additional cost they demand
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost){
        StringBuilder stringBuilder = new StringBuilder(String.format(ADDITIONAL_CARDS_ARE, cardsString(drawnCards)));
        if(additionalCost > 0){
            stringBuilder.append(String.format(SOME_ADDITIONAL_COST, additionalCost, plural(additionalCost)));
        }
        else{
            stringBuilder.append(NO_ADDITIONAL_COST);
        }
        return stringBuilder.toString();
    }

    /**
     * Returns a message saying that this instance's did not claim a route.
     * @param route
     *          route not claimed
     * @return a message saying that this instance's did not claim a route
     */
    public String didNotClaimRoute(Route route){
        return String.format(DID_NOT_CLAIM_ROUTE, playerName, routeString(route));
    }

    /**
     * Returns a message saying that the last turns begins and indicates the number of cards of this instance's player.
     * @param cardCount
     *          card of the player
     * @return a message saying that the last turns begins and indicates the number of cards of this instance's player
     */
    public String lastTurnBegins(int cardCount){
        return String.format(LAST_TURN_BEGINS, playerName, cardCount, plural(cardCount));
    }

    /**
     * Returns a message indicating who won the longest trail bonus.
     * @param longestTrail
     *          the longest trail
     * @return a message indicating who won the longest trail bonus
     */
    public String getsLongestTrailBonus(Trail longestTrail){
        String trailString = longestTrail.station1() + EN_DASH_SEPARATOR + longestTrail.station2();
        return String.format(GETS_BONUS, playerName, trailString);
    }

    /**
     * Returns a message declaring that this instance's player won, together with his points and the loser's points.
     * @param points
     *          winner's points
     * @param loserPoints
     *          loser's points
     * @return a message declaring that this instance's player won, together with his points and the loser's points
     */
    public String won(int points, int loserPoints){
        return String.format
                (WINS,
                        playerName, points, plural(points),
                        loserPoints, plural(loserPoints));
    }

    /**
     * Returns a well formatted string for multiple cards.
     * @param cards
     * @return a well formatted string for multiple cards
     */
    private String cardsString(SortedBag<Card> cards){
        StringBuilder cardStringBuilder = new StringBuilder();
        ArrayList<String> cardsArray = new ArrayList<>();

        for (Card c : cards.toSet()) {
            int cCount = cards.countOf(c);
            cardsArray.add(cCount + " " + cardName(c, cCount));
        }
        if(cardsArray.size() > 1){
            cardStringBuilder.append(String.join(", ", cardsArray.subList(0, cardsArray.size()-1)));
            cardStringBuilder.append(AND_SEPARATOR);
        }

        return cardStringBuilder.append(cardsArray.get(cardsArray.size()-1)).toString();
    }

    /**
     * Returns a well formatted string for a route.
     * @param route
     * @return a well formatted string for a route
     */
    private String routeString(Route route){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(route.station1())
                .append(EN_DASH_SEPARATOR)
                .append(route.station2());

        return stringBuilder.toString();
    }

}