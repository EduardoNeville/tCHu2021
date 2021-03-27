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


    public static void play(Map<PlayerId,Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() ==2);

        //Map with the players' Info classes
        Map<Player, Info> pInfo = new HashMap<>();

        PlayerHandler pHandler = new PlayerHandler(players);

        //give the players the names of all the players and init the Info classes with the names
        for (PlayerId id: players.keySet()){
            players.get(id).initPlayers(id, playerNames);
            pInfo.put(players.get(id), new Info(playerNames.get(id)));
        }


        //init gameState
        GameState gameState = GameState.initial(tickets, rng);

        //tell players who plays first
        PlayerId currentPlayerId = gameState.currentPlayerId();
        players.forEach((p,t) -> t.receiveInfo(pInfo.get(players.get(currentPlayerId)).willPlayFirst())); //is this the good way to do?

        //asks players tickets to keep
        for (PlayerId id: players.keySet()) {
            Player p = players.get(id);

            //choices
            p.setInitialTicketChoice(gameState.topTickets(INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(INITIAL_TICKETS_COUNT); //remove tickets from deck

            pHandler.updateState(gameState);
            SortedBag<Ticket> initialTicketsChosen = p.chooseInitialTickets();
            gameState = gameState.withInitiallyChosenTickets(id, initialTicketsChosen);
            p.receiveInfo(pInfo.get(p).keptTickets(initialTicketsChosen.size()));
        }


        //main game loop
        while (true){ //TODO: put ending conditions
            Player currentPlayer = players.get(currentPlayerId);
            pHandler.updateState(gameState);


            switch(currentPlayer.nextTurn()){

                case DRAW_CARDS:
                    SortedBag<Ticket> initialTickets = gameState.topTickets(IN_GAME_TICKETS_COUNT);
                    pHandler.receiveInfo(pInfo.get(currentPlayer).drewTickets(IN_GAME_TICKETS_COUNT));

                    SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(initialTickets);
                    gameState = gameState.withChosenAdditionalTickets(initialTickets, chosenTickets);
                    pHandler.receiveInfo(pInfo.get(currentPlayer).keptTickets(chosenTickets.size()));

                    break;

                case CLAIM_ROUTE:
            }



        }


    }


    /**
     * Handler that handles the info messages and updates the players game states
      */
    private static class PlayerHandler{
        private Map<PlayerId, Player> players;

        public PlayerHandler(Map<PlayerId, Player> players) {
            this.players = players;
        }

        /**
         * Updates players with the new game state
         * @param newGameState
         *          new game state
         */
        private void updateState(GameState newGameState){
            players.forEach((p,t) -> t.updateState(newGameState, newGameState.playerState(p)));
        }

        /**
         * Give info to players
         * @param message
         *          info to give to players
         */
        private void receiveInfo(String message){
            players.values().forEach((t) -> t.receiveInfo(message));
        }

    }

    // In case we dont end up using the PlayerHandler class
//    private static void updateState(Map<PlayerId, Player> map, GameState newGameState){
//        map.forEach((p,t) -> t.updateState(newGameState, newGameState.playerState(p)));
//    }
//
//    private static void receiveInfo(Map<PlayerId, Player> map, String message){
//        map.forEach((p,t) -> t.receiveInfo(message));
//
//    }

}
