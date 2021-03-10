package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * Deck Class
 * Methods: of, size, isEmpty, topCard, withoutTopCard, topCards, withoutTopCards.
 * Constructor: (Private) Deck Constructor
 *
 * @author Eduardo Neville
 *
 */
public final class Deck <C extends Comparable<C>> {

    private final List<C> DeckofCards;

    private Deck(List<C> sortedBag){
        this.DeckofCards = sortedBag;
    }

    /**
     * Shuffler for the cards
     * @param cards given cards
     * @param rng random variable
     * @param <C>
     * @return Shuffled cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){
        var PostShuffle = new ArrayList<>(cards.toList());
        Collections.shuffle(PostShuffle,rng);
        return new Deck<>(PostShuffle) ;
    }

    /**
     * Getter for the size of the card deck
     * @return Card deck size
     */
    public int size(){
        return DeckofCards.size();
    }

    /**
     * Check if the deck is empty
     * @return true if deck is empty
    */
    public boolean isEmpty(){
        return DeckofCards.size() == 0;
    }

    /**
     * Method topCard finds the top card in the deck
     * @return topCard of the deck
    */
    public C topCard(){
        Preconditions.checkArgument(DeckofCards.isEmpty());
        return new Deck<>(DeckofCards.subList(0,0));
    }

    /**
     * Method returns the DeckofCards without the top card
     * @return DeckofCards without the top card
     */
    public Deck<C> withoutTopCard(){
        Preconditions.checkArgument(DeckofCards.isEmpty());
        return new Deck<>(DeckofCards.subList(1, DeckofCards.size()));
    }

    /**
     * Method returns the first (count) top cards
     * @param count # of cards from the top
     * @return The first (count) top cards
     */
    public SortedBag<C> topCards(int count){
        Preconditions.checkArgument(DeckofCards.isEmpty());
        return new Deck<>(DeckofCards.subList(0, count));
    }

    /**
     * Method returns the DeckofCards without the first (count) top cards
     * @param count # of cards removed from the top
     * @return DeckofCards without the first (count) top cards
     */
    public Deck<C> withoutTopCards(int count){
        Preconditions.checkArgument(DeckofCards.isEmpty());
        return new Deck<>(DeckofCards.subList(count, DeckofCards.size()-count));
    }
}
