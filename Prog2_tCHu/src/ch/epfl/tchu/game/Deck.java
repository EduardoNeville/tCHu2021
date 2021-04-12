package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * Deck Class
 * Methods: of, size, isEmpty, topCard, withoutTopCard, topCards, withoutTopCards
 * Constructor: Deck Constructor (private)
 *
 * @author Eduardo Neville (314667)
 */
public final class Deck <C extends Comparable<C>> {
    private final List<C> deckofCards;

    private Deck(List<C> sortedBag){
        this.deckofCards = List.copyOf(sortedBag);
    }

    /**
     * Shuffler for the cards
     * @param cards
     *          given cards
     * @param rng
     *          random variable
     * @param <C>
     *          type of card the deck has
     * @return Shuffled cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){
        var PostShuffle = new ArrayList<>(cards.toList());
        Collections.shuffle(PostShuffle,rng);
        return new Deck<>(PostShuffle);
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
        return size()==0;
    }

    /**
     * Method topCard finds the top card in the deck
     * @return topCard of the deck
     */
    public C topCard(){
        Preconditions.checkArgument(!isEmpty());
        return deckofCards.get(0);
    }

    /**
     * Method returns the DeckofCards without the top card
     * @return DeckofCards without the top card
     */
    public Deck<C> withoutTopCard(){
        Preconditions.checkArgument(!deckofCards.isEmpty());
        return new Deck<>(deckofCards.subList(1, deckofCards.size()));
    }

    /**
     * Method returns the first (count) top cards
     * @param count
     *          # of cards from the top
     * @throws IllegalArgumentException
     *          throw if count is smaller that 0 or bigger that the size of the deck
     * @return The first (count) top cards
     */
    public SortedBag<C> topCards(int count){
        Preconditions.checkArgument(0 <= count && count <= size());
        return SortedBag.of(deckofCards.subList(0, count));
    }

    /**
     * Method returns the DeckofCards without the first (count) top cards
     * @param count
     *          # of cards removed from the top
     * @return DeckofCards without the first (count) top cards
     */
    public Deck<C> withoutTopCards(int count){
        Preconditions.checkArgument(0 <= count && count <= deckofCards.size());
        return new Deck<>(deckofCards.subList(count, deckofCards.size()));
    }
}
