package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Random;

public final class GameState {

    public static GameState initial(SortedBag<Ticket> tickets, Random rng){
        Preconditions.checkArgument();
        return new PlayerState()
    }

    public PlayerState playerState(PlayerId playerId){

    }


    public SortedBag<Ticket> topTickets(int count){

    }

    public GameState withoutTopTickets(int count){

    }

}
