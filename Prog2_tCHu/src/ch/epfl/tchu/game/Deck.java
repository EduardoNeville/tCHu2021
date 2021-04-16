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
public final class Deck<C extends Comparable<C>> {
    private final List<C> deckOfCards;

    private Deck(List<C> sortedBag) {
        this.deckOfCards = List.copyOf(sortedBag);
    }

    /**
     * Shuffler for the cards
     *
     * @param cards given cards
     * @param rng   random variable
     * @param <C>   type of card the deck has
     * @return Shuffled cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        var PostShuffle = new ArrayList<>(cards.toList());
        Collections.shuffle(PostShuffle, rng);
        return new Deck<>(PostShuffle);
    }

    /**
     * Getter for the size of the card deck
     *
     * @return Card deck size
     */
    public int size() {
        return deckOfCards.size();
    }

    /**
     * Check if the deck is empty
     *
     * @return true if deck is empty
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Method topCard finds the top card in the deck
     *
     * @return topCard of the deck
     * @throws IllegalArgumentException thrown if deck is empty
     */
    public C topCard() {
        Preconditions.checkArgument(!isEmpty());
        return deckOfCards.get(0);
    }

    /**
     * Method returns the DeckofCards without the top card
     *
     * @return DeckofCards without the top card
     * @throws IllegalArgumentException thrown if deckofCards is empty
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!deckOfCards.isEmpty());
        return new Deck<>(deckOfCards.subList(1, deckOfCards.size()));
    }

    /**
     * Method returns the first (count) top cards
     *
     * @param count # of cards from the top
     * @return The first (count) top cards
     * @throws IllegalArgumentException thrown if count is smaller that 0 or bigger that the size of the deck
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(0 <= count && count <= size());
        return SortedBag.of(deckOfCards.subList(0, count));
    }

    /**
     * Method returns the DeckofCards without the first (count) top cards
     *
     * @param count # of cards removed from the top
     * @return DeckofCards without the first (count) top cards
     * @throws IllegalArgumentException thrown if count isn't between 0 and the deckofCards size
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(0 <= count && count <= deckOfCards.size());
        return new Deck<>(deckOfCards.subList(count, deckOfCards.size()));
    }
}
