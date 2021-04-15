package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
//import ch.epfl.tchu.game.Constants.*;
import ch.epfl.tchu.game.Player.TurnKind.*;
import ch.epfl.tchu.gui.StringsFr;
import com.sun.source.tree.Tree;


import java.util.*;

import static ch.epfl.tchu.game.Constants.*;

/**
 * Game of Tchu.
 *
 * @author Martin Sanchez Lopez (313238)
 */
public final class Game {


    /**
     * Plays a game with the given players and tickets and rng.
     *
     * @param players     map of the id and their player instances
     * @param playerNames map of the id and their player names
     * @param tickets     bag of the tickets of the game
     * @param rng         random number generator
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

        //Map with the players' Info classes
        Map<PlayerId, Info> pInfo = new HashMap<>();


        //give the players the names of all the players and init the Info classes with the names
        for (PlayerId id : players.keySet()) {
            players.get(id).initPlayers(id, playerNames);
            pInfo.put(id, new Info(playerNames.get(id)));
        }


        //init gameState
        GameState gameState = GameState.initial(tickets, rng);

        PlayerId currentPlayerId = gameState.currentPlayerId();
        Player currentPlayer; //initialized for the big loop

        //tells players who plays first
        receiveInfo(players, pInfo.get(currentPlayerId).willPlayFirst());

        //tells players initial given tickets
        for (Player p : players.values()) {
            p.setInitialTicketChoice(gameState.topTickets(INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(INITIAL_TICKETS_COUNT); //remove tickets from deck
        }

        Map<PlayerId, Integer> ticketsKept = new TreeMap<>();
        //asks players tickets to keep
        for (PlayerId id : players.keySet()) {
            Player p = players.get(id);
            updateState(players, gameState);

            SortedBag<Ticket> initialTicketsChosen = p.chooseInitialTickets();
            int amountOfTickets = initialTicketsChosen.size();
            gameState = gameState.withInitiallyChosenTickets(id, initialTicketsChosen);
            ticketsKept.put(id, amountOfTickets);
        }

        //give info of tickets kept by players
        ticketsKept.forEach((id, n) -> receiveInfo(players, pInfo.get(id).keptTickets(n)));

        //main game loop
        for (; ; ) {
            currentPlayerId = gameState.currentPlayerId();
            currentPlayer = players.get(currentPlayerId);

            System.out.println("gameState.cardState().deckSize() " + gameState.cardState().deckSize());
            System.out.println("gameState.cardState().discardsSize() " + gameState.cardState().discardsSize());
            System.out.println("gameState.playerState(PlayerId.PLAYER_1).cardCount()" + gameState.playerState(PlayerId.PLAYER_1).cardCount());
            System.out.println("gameState.playerState(PlayerId.PLAYER_2).cardCount()" + gameState.playerState(PlayerId.PLAYER_2).cardCount());

            System.out.println("Le nombre de cartes en jeux est : " +
                    (gameState.cardState().deckSize() + gameState.cardState().discardsSize()
                            + gameState.playerState(PlayerId.PLAYER_1).cardCount()
                            + gameState.playerState(PlayerId.PLAYER_2).cardCount()
                            + 5));

            updateState(players, gameState);
            receiveInfo(players, pInfo.get(currentPlayerId).canPlay());
            switch (currentPlayer.nextTurn()) {
                case DRAW_TICKETS:
                    SortedBag<Ticket> initialTickets = gameState.topTickets(IN_GAME_TICKETS_COUNT);
                    receiveInfo(players, pInfo.get(currentPlayerId).drewTickets(IN_GAME_TICKETS_COUNT));

                    SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(initialTickets);
                    gameState = gameState.withChosenAdditionalTickets(initialTickets, chosenTickets);
                    receiveInfo(players, pInfo.get(currentPlayerId).keptTickets(chosenTickets.size()));
                    break;

                case DRAW_CARDS:
                    for (int i = 0; i < 2; i++) {
                        //check deck not empty
                        if (gameState.cardState().isDeckEmpty()) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        }
                        updateState(players, gameState);

                        int slot = currentPlayer.drawSlot();
                        if (slot == -1) {
                            //gameState.currentPlayerState().withAddedCard(gameState.topCard());
                            gameState = gameState.withBlindlyDrawnCard();
                            receiveInfo(players, pInfo.get(currentPlayerId).drewBlindCard());
                        } else {
                            Card faceUpCard = gameState.cardState().faceUpCard(slot);
                            //gameState.currentPlayerState().withAddedCard(faceUpCard);
                            gameState = gameState.withDrawnFaceUpCard(slot);
                            receiveInfo(players, pInfo.get(currentPlayerId).drewVisibleCard(faceUpCard));
                        }
                    }
                    break;

                case CLAIM_ROUTE:
                    Route chosenRoute = currentPlayer.claimedRoute();
                    SortedBag<Card> initCards = currentPlayer.initialClaimCards();

                    //by default no additional cards needed, changes if route is underground and additional cards are needed
                    SortedBag<Card> drawnCards = SortedBag.of();
                    SortedBag<Card> additionalCards = SortedBag.of();
                    int additionalCardsCount = 0;

                    if (chosenRoute.level() == Route.Level.UNDERGROUND) {
                        receiveInfo(players, pInfo.get(currentPlayerId).attemptsTunnelClaim(chosenRoute, initCards));

                        SortedBag.Builder<Card> additionalCardsTunnelBuilder = new SortedBag.Builder<>();
                        //draw additional tunnel cards one by one...
                        for (int i = 0; i < ADDITIONAL_TUNNEL_CARDS; i++) {
                            //check deck not empty
                            if (gameState.cardState().isDeckEmpty()) {
                                gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            }
                            additionalCardsTunnelBuilder.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }
                        drawnCards = additionalCardsTunnelBuilder.build();
                        additionalCardsCount = chosenRoute.
                                additionalClaimCardsCount(initCards, drawnCards);

                        receiveInfo(players, pInfo.get(currentPlayerId).drewAdditionalCards(drawnCards, additionalCardsCount));


                        //player has to play additional cards
                        if (additionalCardsCount >= 1) {
                            List<SortedBag<Card>> possibleAdditionalCards = gameState.
                                    currentPlayerState().
                                    possibleAdditionalCards(additionalCardsCount, initCards, drawnCards);

                            if (!possibleAdditionalCards.isEmpty())
                                additionalCards = currentPlayer.chooseAdditionalCards(possibleAdditionalCards);
                        }

                    }
                    if (additionalCardsCount == 0 || !additionalCards.isEmpty()) {
                        SortedBag<Card> finalClaimCards = initCards.union(additionalCards);
                        System.out.println("claim : " + finalClaimCards + " draw : " + drawnCards);
                        gameState = gameState
                                .withClaimedRoute(chosenRoute, finalClaimCards)
                                .withMoreDiscardedCards(drawnCards);

                        receiveInfo(players, pInfo.get(currentPlayerId).claimedRoute(chosenRoute, finalClaimCards));
                    } else {
                        gameState = gameState.withMoreDiscardedCards(drawnCards);
                        receiveInfo(players, pInfo.get(currentPlayerId).didNotClaimRoute(chosenRoute));
                    }
                    break;
            }

            //ending condition
            if (gameState.lastPlayer() != null && gameState.lastPlayer() == currentPlayerId) {
                break;
            }

            //last turn announcement
            if (gameState.lastTurnBegins())
                receiveInfo(players, pInfo.get(currentPlayerId).lastTurnBegins(gameState.currentPlayerState().carCount()));

            //change current player
            gameState = gameState.forNextTurn();
        }

        //final update
        updateState(players, gameState);

        //find longest Route(s) and give bonus info
        Set<PlayerId> longestTrailPossessors = longestRouteWinners(players, gameState, pInfo);
        System.out.println("Trail calculed");

        int maxPoints = Integer.MIN_VALUE;
        int loserPoints = Integer.MIN_VALUE;
        List<PlayerId> gameWinners = new ArrayList<>();

        //calculate the points of each players and determine the winner at same time
        for (PlayerId id : players.keySet()) {
            System.out.println("entered for loop");
            int p = gameState.playerState(id).finalPoints();
            if (longestTrailPossessors.contains(id)) {
                p += LONGEST_TRAIL_BONUS_POINTS;
            }
            System.out.println(p + " maxpoints : " + maxPoints);


            if (p == maxPoints) {
                gameWinners.add(id);
            } else if (p > maxPoints) {
                loserPoints = maxPoints;
                maxPoints = p;
                gameWinners = new ArrayList<>(List.of(id));
            } else { // p < maxPoints
                loserPoints = p;
            }
            System.out.println("maxp : " + maxPoints + "  loserp : " + loserPoints + " setsize : " + gameWinners.size());
        }

        //declare winner
        if (gameWinners.size() == 2) {
            receiveInfo(players, Info.draw(new ArrayList<>(playerNames.values()), maxPoints));
        } else {
            receiveInfo(players, pInfo.get(gameWinners.get(0)).won(maxPoints, loserPoints));
        }

    }


    /**
     * Calculates the player(s) that have the longest length Trail, output the message that they won the bonus and
     * returns the players that must be given the points for the bonus
     *
     * @param players     the game playres
     * @param gameState   game state
     * @param playersInfo players' Info instances
     * @return set of players that are to obtain longestRoute bonus
     */
    private static Set<PlayerId> longestRouteWinners(Map<PlayerId, Player> players, GameState gameState, Map<PlayerId, Info> playersInfo) {

        Map<PlayerId, Trail> longestTrails = new HashMap<>();
        int longestLength = 0;
        Set<PlayerId> bonusWinners = new HashSet<>();

        //calculate the longests trails of all the players and find the winner(s)
        for (PlayerId id : players.keySet()) {
            Trail playerLongest = Trail.longest(gameState.playerState(id).routes());
            int l = playerLongest.length();

            if (l == longestLength) {
                bonusWinners.add(id);
            } else if (l > longestLength) {
                longestLength = l;
                bonusWinners = new HashSet<>(Set.of(id));
            }
            longestTrails.put(id, playerLongest);
        }

        //declare winners of longest trail bonus
        bonusWinners.forEach(id -> receiveInfo(
                players, playersInfo.get(id).getsLongestTrailBonus(
                        longestTrails.get(id)))
        );

        return bonusWinners;
    }

    /**
     * Updates players with the new game state
     *
     * @param newGameState new game state
     */
    private static void updateState(Map<PlayerId, Player> map, GameState newGameState) {
        map.forEach((id, p) -> p.updateState(newGameState, newGameState.playerState(id)));
    }

    /**
     * Give info to players
     *
     * @param message info to give to players
     */
    private static void receiveInfo(Map<PlayerId, Player> map, String message) {
        map.values().forEach((p) -> p.receiveInfo(message));
    }

}
