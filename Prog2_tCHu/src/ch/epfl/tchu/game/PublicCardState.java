package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static ch.epfl.tchu.game.Constants.FACE_UP_CARDS_COUNT;

/**
 *This class represents (part of) the state of the wagon
 * / locomotive cards that are not in the players' hand
 */
public class PublicCardState {

    private final int deckSize;
    private final int discardsSize;
    private final List<Card> faceUpCards;


    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize){
        Preconditions.checkArgument(faceUpCards.size()== FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(deckSize>=0);
        Preconditions.checkArgument(discardsSize>=0);

        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
        this.faceUpCards = List.copyOf(faceUpCards);    }

    /**
     *
     * @return the total number of cards that are not in the players' hand, namely the 5 with the face up, those from the draw pile and those from the discard pile
     */
    public int totalSize(){
        return (FACE_UP_CARDS_COUNT + deckSize + discardsSize);
    }

    /**
     * @return the 5 cards face up, in the form of a list containing exactly 5 elements
     */
    public List<Card> faceUpCards(){
        return faceUpCards;
    }

    /**
     * @return the card face up at the given index
     */
    public Card faceUpCard(int slot){
        Objects.checkIndex(slot, FACE_UP_CARDS_COUNT);
        return faceUpCards.get(slot);
    }

    /**
     * @return the deckSize
     */
    public int deckSize(){
        return deckSize;
    }

    public boolean isDeckEmpty(){
        return deckSize==0;
    }

    public int discardsSize(){
        return discardsSize;
    }

}
