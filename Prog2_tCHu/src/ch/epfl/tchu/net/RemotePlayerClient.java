package ch.epfl.tchu.net;

import ch.epfl.tchu.game.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class RemotePlayerClient{
    private final Player player;




    public RemotePlayerClient(Socket socket, Player player, BufferedReader reader) throws IOException {
        this.player = player;

    }

    public void run(){
        
    }



}
