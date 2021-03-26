package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import ch.epfl.tchu.gui.Info;
import ch.epfl.tchu.game.Constants.*;
import ch.epfl.tchu.gui.StringsFr;
import com.sun.source.tree.Tree;

import java.util.*;

import static ch.epfl.tchu.game.Constants.*;

public final class Game {


    public static void play(Map<PlayerId,Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() ==2);

        //Map with the players' Info classes
        Map<Player, Info> pInfo = new HashMap<>();

        //give the players the names of all the players and init the Info classes with the names
        for (PlayerId id: players.keySet()){
            players.get(id).initPlayers(id, playerNames);
            pInfo.put(players.get(id), new Info(playerNames.get(id)));
        }


        //init gameState
        GameState gameState = GameState.initial(tickets, rng);

        //tell players who plays first
        PlayerId firstPlayer = gameState.currentPlayerId();
        players.forEach((p,t) -> t.receiveInfo(pInfo.get(players.get(firstPlayer)).willPlayFirst())); //is this the good way to do?

        //asks players tickets to keep
        for (Player p: players.values()) {
            p.setInitialTicketChoice(gameState.topTickets(INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(INITIAL_TICKETS_COUNT); //remove tickets from deck
            p.receiveInfo(pInfo.get(p).keptTickets(p.chooseInitialTickets().size()));
        }




    }




}
