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


        //main game loop
        while (true) { //TODO: put ending conditions
            Player currentPlayer = players.get(currentPlayerId);
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

            //change current player
            gameState = gameState.forNextTurn();
        }


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
