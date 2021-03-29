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

public final class Game {


    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

        //Map with the players' Info classes
        Map<Player, Info> pInfo = new HashMap<>();


        //give the players the names of all the players and init the Info classes with the names
        for (PlayerId id : players.keySet()) {
            players.get(id).initPlayers(id, playerNames);
            pInfo.put(players.get(id), new Info(playerNames.get(id)));
        }


        //init gameState
        GameState gameState = GameState.initial(tickets, rng);

        //tells players who plays first
        PlayerId currentPlayerId = gameState.currentPlayerId();
        players.forEach((p, t) -> t.receiveInfo(pInfo.get(players.get(currentPlayerId)).willPlayFirst())); //is this the good way to do?

        //tells players initial given tickets
        for(Player p: players.values())
            p.setInitialTicketChoice(gameState.topTickets(INITIAL_TICKETS_COUNT));
           gameState = gameState.withoutTopTickets(INITIAL_TICKETS_COUNT); //remove tickets from deck


//        for (PlayerId id : players.keySet()) {
//            Player p = players.get(id);
//
//            updateState(players, gameState);
//            SortedBag<Ticket> initialTicketsChosen = p.chooseInitialTickets();
//            gameState = gameState.withInitiallyChosenTickets(id, initialTicketsChosen);
//            p.receiveInfo(pInfo.get(p).keptTickets(initialTicketsChosen.size()));
//        }
        //asks players tickets to keep
        for (Map.Entry<PlayerId, Player> entry : players.entrySet()) {
            Player p = entry.getValue();
            updateState(players, gameState);
            SortedBag<Ticket> initialTicketsChosen = p.chooseInitialTickets();
            gameState = gameState.withInitiallyChosenTickets(entry.getKey(), initialTicketsChosen);
            p.receiveInfo(pInfo.get(p).keptTickets(initialTicketsChosen.size()));
        }

        boolean gameHasEnded = false;

        //main game loop
        while (!gameHasEnded) {
            Player currentPlayer = players.get(currentPlayerId);

            if(gameState.lastPlayer() != null)
                receiveInfo(receiveInfo(players, ));

            receiveInfo(players, pInfo.get(currentPlayer).canPlay());
            // USEFUL ?? updateState(players, gameState);

            updateState(players, gameState);
            switch (currentPlayer.nextTurn()) {

                case DRAW_TICKETS:

                    SortedBag<Ticket> initialTickets = gameState.topTickets(IN_GAME_TICKETS_COUNT);
                    receiveInfo(players, pInfo.get(currentPlayer).drewTickets(IN_GAME_TICKETS_COUNT));

                    SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(initialTickets);
                    gameState = gameState.withChosenAdditionalTickets(initialTickets, chosenTickets);
                    receiveInfo(players, pInfo.get(currentPlayer).keptTickets(chosenTickets.size()));

                    break;

                case DRAW_CARDS: //TODO: updates and messages
                    for (int i = 0; i < 2; i++) {
                        //check deck not empty
                        if (gameState.cardState().isDeckEmpty()) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        }
                        updateState(players, gameState);

                        int slot = currentPlayer.drawSlot();
                        if (slot == -1) {
                            gameState.currentPlayerState().withAddedCard(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                            receiveInfo(players, pInfo.get(currentPlayer).drewBlindCard());
                        } else {
                            Card faceUpCard = gameState.cardState().faceUpCard(slot);
                            gameState.currentPlayerState().withAddedCard(faceUpCard);
                            gameState = gameState.withDrawnFaceUpCard(slot);
                            receiveInfo(players, pInfo.get(currentPlayer).drewVisibleCard(faceUpCard));
                        }
                    }
                    break; //TODO: useless?

                case CLAIM_ROUTE:
                    Route chosenRoute = currentPlayer.claimedRoute();
                    //TODO: do we have to check all this here??? piazza answers are unclear
                    boolean canClaimRoute = gameState.currentPlayerState().canClaimRoute(chosenRoute);
                    if (canClaimRoute) {

                        //TODO: check if initialCards are valid to start claim???
                        SortedBag<Card> initCards = currentPlayer.initialClaimCards();

                        //by default no additional cards needed, changes if route is underground and additional cards are needed
                        SortedBag<Card> additionalCards = SortedBag.of();
                        int additionalCardsCount = 0;

                        if (chosenRoute.level() == Route.Level.UNDERGROUND) {
                            receiveInfo(players, pInfo.get(currentPlayer).attemptsTunnelClaim(chosenRoute, initCards));
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
                            SortedBag<Card> drawnCards = additionalCardsTunnelBuilder.build();
                            additionalCardsCount = chosenRoute.
                                    additionalClaimCardsCount(initCards, drawnCards);

                            receiveInfo(players, pInfo.get(currentPlayer).drewAdditionalCards(drawnCards, additionalCardsCount));

                            //player has to play additional cards
                            if (additionalCardsCount >= 1) {
                                List<SortedBag<Card>> possibleAdditionalCards = gameState.
                                        currentPlayerState().
                                        possibleAdditionalCards(additionalCardsCount, initCards, drawnCards);

                                additionalCards = currentPlayer.chooseAdditionalCards(possibleAdditionalCards);
                            }


                        }
                        if (!additionalCards.isEmpty() || additionalCardsCount == 0) {
                            SortedBag<Card> finalClaimCards = initCards.union(additionalCards);
                            gameState = gameState.
                                    withClaimedRoute(chosenRoute, finalClaimCards);
                            receiveInfo(players, pInfo.get(currentPlayer).claimedRoute(chosenRoute, finalClaimCards));
                            updateState(players, gameState);
                        } else
                            receiveInfo(players, pInfo.get(currentPlayer).didNotClaimRoute(chosenRoute));

                    }

                    break;
            }


            //ending condition
            if (gameState.lastPlayer() != null && gameState.lastPlayer() == currentPlayerId) {
                gameHasEnded = true;
            }

            //last turn announcement
            if (gameState.lastTurnBegins())
                receiveInfo(players, pInfo.get(currentPlayer).lastTurnBegins(gameState.currentPlayerState().carCount()));

            //change current player
            gameState = gameState.forNextTurn();
        }


        updateState(players, gameState);
        //each number of points has a set of players that got that exact number
        Map<Integer, Set<PlayerId>> points = new TreeMap<>();

        Set<PlayerId> longestTrailPossessors = longestRoute(players, gameState, pInfo);

        GameState finalGameState = gameState;
        players.keySet().forEach(id -> {
            int pointsBeforeLongest = finalGameState.playerState(id).finalPoints();
            if (longestTrailPossessors.contains(id))
                pointsBeforeLongest+=LONGEST_TRAIL_BONUS_POINTS;

            Set<PlayerId> ids = points.getOrDefault(id, new TreeSet<>());
            ids.add(id);

            points.put(pointsBeforeLongest, ids);
        });

        int maxPoints = -9999; // in case all have negative points

        //set max poitns
        for (int p : points.keySet()) {
            if (p>maxPoints)
                maxPoints = p;
        }

        List<PlayerId> winners= new ArrayList<>(points.get(maxPoints));

//        winners.size() == 2 ?
//                receiveInfo(players, pInfo.get(winners.get(0)).won(maxPoints, )),
//                receiveInfo(players, Info.draw(winners, maxPoints));


    }


    /**
     * Calculates the player(s) that have the longest length Trail, output the message that they got the bonus and
     * returns the players that must be given the points for the bonus
     *
     * @param players
     *          the game playres
     * @param gameState
     *          game state
     * @param playersInfo
     *          players' Info instances
     *
     * @return set of players that are to obtain longestRoute bonus
     */
    private static Set<PlayerId> longestRoute(Map<PlayerId, Player> players, GameState gameState, Map<Player, Info> playersInfo){

        //NOTE: I am aware that this is possibly overkill but it was done so that it
        // would work with any number of players seamlessly (TAs seem to bring up that point often)

        //TODO: simplify this whole thing

        Map<PlayerId, Trail> longestTrails = new HashMap<>();
        int longestLength = 0;

        //get every playre's longest length
        players.forEach((id, p) -> {
            Trail t = Trail.longest(gameState.playerState(id).routes());
            longestTrails.put(id,t);
        });

        Map<Integer, Set<PlayerId>> lengths = new TreeMap<>();

        //map the players to their longest route lengths
        longestTrails.forEach( (id, t) -> {
            int l = longestTrails.get(id).length();
            Set<PlayerId> ids = lengths.getOrDefault(t, new TreeSet<>());
            ids.add(id);
            lengths.put(l, ids);
        });

        //find the longest length
        for (int l : lengths.keySet()) {
            if (l > longestLength)
                longestLength = l;
        }

        //declare the longest length bonus for every player that has a trail of the longest length
        lengths.get(longestLength).forEach(id ->
                receiveInfo(
                        players, playersInfo.get(id).getsLongestTrailBonus(
                                longestTrails.get(id)
                        )));

        return lengths.get(longestLength);
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
        map.forEach((id, p) -> p.receiveInfo(message));
        map.values().forEach((p) -> p.receiveInfo(message));
    }

}
