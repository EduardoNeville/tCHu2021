package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * Deck Class
 * Methods: of, size, isEmpty, topCard, withoutTopCard, topCards, withoutTopCards
 * Constructor: Deck Constructor (private)
 *
 * @author Eduardo Neville
 */
public final class Deck <C extends Comparable<C>> {

    private final List<C> deckofCards;

    /**
     * Private constructor to return the deckofCards
     * @param sortedBag
     */
    private Deck(List<C> sortedBag){
        this.deckofCards = sortedBag;
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
        return deckofCards.size();
    }

    /**
     * Check if the deck is empty
     * @return true if deck is empty
    */
    public boolean isEmpty(){
        return deckofCards.isEmpty(); //is it returning true here?
    }

    /**
     * Method topCard finds the top card in the deck
     * @return topCard of the deck
    */
    public C topCard(){
        Preconditions.checkArgument(deckofCards.isEmpty());
        return deckofCards.get(0); //to be fixed
    }

    /**
     * Method returns the DeckofCards without the top card
     * @return DeckofCards without the top card
     */
    public Deck<C> withoutTopCard(){
        Preconditions.checkArgument(deckofCards.isEmpty());
        return new Deck<>(deckofCards.subList(1, deckofCards.size()));
    }

    /**
     * Method returns the first (count) top cards
     * @param count # of cards from the top
     * @return The first (count) top cards
     */
    public SortedBag<C> topCards(int count){
        Preconditions.checkArgument(deckofCards.isEmpty());
        SortedBag<C> topCards = new SortedBag<>(deckofCards.subList(0, count));
        return topCards;// to be fixed
    }

    /**
     * Method returns the DeckofCards without the first (count) top cards
     * @param count # of cards removed from the top
     * @return DeckofCards without the first (count) top cards
     */
    public Deck<C> withoutTopCards(int count){
        Preconditions.checkArgument(!(0 <= count && count <= deckofCards.size()));
        return new Deck<>(deckofCards.subList(count, deckofCards.size()-count));
    }
}
