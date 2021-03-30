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
        Map<PlayerId, Info> pInfo = new HashMap<>();


        //give the players the names of all the players and init the Info classes with the names
        for (PlayerId id : players.keySet()) {
            players.get(id).initPlayers(id, playerNames);
            pInfo.put(id, new Info(playerNames.get(id)));
        }


        //init gameState
        GameState gameState = GameState.initial(tickets, rng);

        //tells players who plays first
        PlayerId currentPlayerId = gameState.currentPlayerId();
        players.forEach((p, t) -> t.receiveInfo(pInfo.get(currentPlayerId).willPlayFirst())); //is this the good way to do?

        //tells players initial given tickets
        for(Player p: players.values())
            p.setInitialTicketChoice(gameState.topTickets(INITIAL_TICKETS_COUNT));
           gameState = gameState.withoutTopTickets(INITIAL_TICKETS_COUNT); //remove tickets from deck

        //TODO: receive info ici?


//        for (PlayerId id : players.keySet()) {
//            Player p = players.get(id);
//
//            updateState(players, gameState);
//            SortedBag<Ticket> initialTicketsChosen = p.chooseInitialTickets();
//            gameState = gameState.withInitiallyChosenTickets(id, initialTicketsChosen);
//            p.receiveInfo(pInfo.get(p).keptTickets(initialTicketsChosen.size()));
//        }
        //asks players tickets to keep
        for (PlayerId id : players.keySet()) {
            Player p = players.get(id);
            updateState(players, gameState);
            SortedBag<Ticket> initialTicketsChosen = p.chooseInitialTickets();
            gameState = gameState.withInitiallyChosenTickets(id, initialTicketsChosen);
            p.receiveInfo(pInfo.get(id).keptTickets(initialTicketsChosen.size()));
        }

        boolean gameHasEnded = false;

        //main game loop
        while (!gameHasEnded) {
            Player currentPlayer = players.get(currentPlayerId);


            receiveInfo(players, pInfo.get(currentPlayerId).canPlay());
            // USEFUL ?? updateState(players, gameState);

            updateState(players, gameState);
            switch (currentPlayer.nextTurn()) {

                case DRAW_TICKETS:

                    SortedBag<Ticket> initialTickets = gameState.topTickets(IN_GAME_TICKETS_COUNT);
                    receiveInfo(players, pInfo.get(currentPlayerId).drewTickets(IN_GAME_TICKETS_COUNT));

                    SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(initialTickets);
                    gameState = gameState.withChosenAdditionalTickets(initialTickets, chosenTickets);
                    receiveInfo(players, pInfo.get(currentPlayerId).keptTickets(chosenTickets.size()));

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
                            receiveInfo(players, pInfo.get(currentPlayerId).drewBlindCard());
                        } else {
                            Card faceUpCard = gameState.cardState().faceUpCard(slot);
                            gameState.currentPlayerState().withAddedCard(faceUpCard);
                            gameState = gameState.withDrawnFaceUpCard(slot);
                            receiveInfo(players, pInfo.get(currentPlayerId).drewVisibleCard(faceUpCard));
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
                            SortedBag<Card> drawnCards = additionalCardsTunnelBuilder.build();
                            additionalCardsCount = chosenRoute.
                                    additionalClaimCardsCount(initCards, drawnCards);

                            receiveInfo(players, pInfo.get(currentPlayerId).drewAdditionalCards(drawnCards, additionalCardsCount));

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
                            receiveInfo(players, pInfo.get(currentPlayerId).claimedRoute(chosenRoute, finalClaimCards));
                            updateState(players, gameState);
                        } else
                            receiveInfo(players, pInfo.get(currentPlayerId).didNotClaimRoute(chosenRoute));

                    }

                    break;
            }


            //ending condition
            if (gameState.lastPlayer() != null && gameState.lastPlayer() == currentPlayerId) {
                gameHasEnded = true;
            }

            //last turn announcement
            if (gameState.lastTurnBegins())
                receiveInfo(players, pInfo.get(currentPlayerId).lastTurnBegins(gameState.currentPlayerState().carCount()));

            //change current player
            gameState = gameState.forNextTurn();
        }


        updateState(players, gameState);
        Set<PlayerId> longestTrailPossessors = longestRouteWinners(players, gameState, pInfo);

        int maxPoints = -9999;
        int loserPoints = -9999;
        List<PlayerId> gameWinners = new ArrayList<>();

        for (PlayerId id : players.keySet()) {
            int p = gameState.playerState(id).finalPoints();
            if (longestTrailPossessors.contains(id)) {
                p+=LONGEST_TRAIL_BONUS_POINTS;
            }

            if(p==maxPoints){
                gameWinners.add(id);
            }
            else if(p>maxPoints){
                loserPoints = maxPoints;
                maxPoints=p;
                gameWinners = new ArrayList<>(List.of(id));
            }
        }

        //declare winner
        if(gameWinners.size() != 1){
            //For draws when player count is >2
            List<String> names = new ArrayList<>();
            gameWinners.forEach(id -> names.add(playerNames.get(id)));
            receiveInfo(players, Info.draw(names, maxPoints));

            //receiveInfo(players, Info.draw(new ArrayList<>(playerNames.values()), maxPoints)); //2 players only
        }
        else{
            receiveInfo(
                    players,
                    pInfo.get(gameWinners.get(0)).won(maxPoints, loserPoints)
            );
        }

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
    private static Set<PlayerId> longestRouteWinners(Map<PlayerId, Player> players, GameState gameState, Map<PlayerId, Info> playersInfo){

        //works for any number of players

        Map<PlayerId, Trail> longestTrails = new HashMap<>();
        int longestLength = 0;
        Set<PlayerId> bonusWinners = new HashSet<>();

        for (PlayerId id :
                players.keySet()) {
            Trail playerLongest = Trail.longest(gameState.playerState(id).routes());
            int l = playerLongest.length();

            if (l == longestLength) {
                bonusWinners.add(id);
            }
            else if (l>longestLength) {
                longestLength=l;
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
        map.forEach((id, p) -> p.receiveInfo(message));
        map.values().forEach((p) -> p.receiveInfo(message));
    }

}
