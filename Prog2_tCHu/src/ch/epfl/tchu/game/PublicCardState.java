package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

/**
 * Public card state. Information about the cards that are accessible to all players, such as the face up cards
 * and the sizes of the deck and discard pile.
 *
 * @author Martin Sanchez Lopez (313238)
 */
public class PublicCardState {

    private final List<Card> faceUpCards;
    private final int faceUpCardsSize;
    private final int deckSize;
    private final int discardsSize;

    /**
     * Constructs a PublicCardState with the given list of faceUpCards, deck size and discards size
     *
     * @param faceUpCards  face up cards
     * @param deckSize     size of the deck of cards
     * @param discardsSize size of the discard pile
     * @throws IllegalArgumentException if faceUpCards' size is equal to FACE_UP_CARDS_COUNT (5 by default)
     * @throws IllegalArgumentException if deckSize is smaller than 0 and discardSize is smaller than 0
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(deckSize >= 0 && discardsSize >= 0);
        this.faceUpCards = List.copyOf(faceUpCards);
        faceUpCardsSize = faceUpCards.size();
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * Returns the total number of cards, the ones face up, the one in the deck and those in the discard.
     * @return the total number of cards, the ones face up, the one in the deck and those in the discard
     */
    public int totalSize() {
        return (faceUpCardsSize + deckSize + discardsSize);
    }

    /**
     * Return a list of the face up cards.
     * @return a list of the face up cards
     */
    public List<Card> faceUpCards() {
        return faceUpCards;
    }

    /**
     * Return the face up card at the given slot.
     *
     * @param slot slot of the card to get
     * @return the face up card at the given slot
     * @throws IndexOutOfBoundsException if the slot number is not between 0 (included) and the number of
     *                                   face up cards (excluded)
     */
    public Card faceUpCard(int slot) {
        Objects.checkIndex(slot, faceUpCardsSize);
        return faceUpCards.get(slot);
    }

    /**
     * Returns true if the deck is empty.
     * @return true if the deck is empty
     */
    public boolean isDeckEmpty() {
        return deckSize == 0;
    }

    /**
     * Returns the number of cards discarded.
     * @return the number of cards discarded
     */
    public int discardsSize() {
        return discardsSize;
    }

    /**
     * Returns the number of cards in the deck.
     * @return the number of cards in the deck
     */
    public int deckSize() {
        return deckSize;
    }
}
