package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * Represents the card state of the game. It consists of the publlic part (PublicCardState) as well as
 * a deck of cards of the game and the discarded cards.
 *
 * @author Eduardo Neville (314667)
 */
public class CardState extends PublicCardState {

    private final Deck<Card> deckOfCards;
    private final SortedBag<Card> discardedCards;

    private CardState(Deck<Card> cardDeck, List<Card> faceUpCards, SortedBag<Card> discarded) {
        super(faceUpCards, cardDeck.size(), discarded.size());

        this.deckOfCards = cardDeck;
        this.discardedCards = discarded;
    }

    /**
     * Creates and returns a card state with the first 5 cards of the given deck as face up cards
     *
     * @param deck Deck we want to find the 5 cards
     * @return Card state with the first 5 cards of given deck as face up cards of this card state
     * @throws IllegalArgumentException If deck is smaller that 5
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() > 4);
        List<Card> faceUp = new ArrayList<>();

        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; ++i) {
            faceUp.add(deck.topCard());
            deck = deck.withoutTopCard();
        }
        return new CardState(deck, faceUp, SortedBag.of());
    }

    /**
     * Returns a new card into the faceUp cards from the deck
     *
     * @param slot the slot take by this new card
     * @return the new set of cards in the table
     * @throws IllegalArgumentException if <code>slot</code> is not between 0 and 4 included
     *                                  or if this state's deck is empty
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, 5);
        Preconditions.checkArgument(!deckOfCards.isEmpty());

        List<Card> newFaceUpCards = new ArrayList<>(faceUpCards());
        newFaceUpCards.remove(slot);
        newFaceUpCards.add(slot, deckOfCards.topCard());
        return new CardState(deckOfCards.withoutTopCard(),
                newFaceUpCards, discardedCards);
    }

    /**
     * Returns the top card of this state's deck, as long at it isn't empty
     *
     * @return Top card of the deck
     * @throws IllegalArgumentException if this card state's deck is empty
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!deckOfCards.isEmpty());
        return deckOfCards.topCard();
    }

    /**
     * CardState without the top deck card
     *
     * @return new cardstate without top card
     * @throws IllegalArgumentException if deckofCards is empty
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!deckOfCards.isEmpty());
        return new CardState(this.deckOfCards.withoutTopCard(), faceUpCards(), discardedCards);
    }

    /**
     * Returns this card state but with a new deck consisting of the discards, reshuffled
     *
     * @param rng random paramenter
     * @return this card state but with a new deck consisting of the discards, reshuffled
     * @throws IllegalArgumentException if this card state's deck is not empty
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(deckOfCards.isEmpty());
        SortedBag<Card> newDiscardedBag = SortedBag.of();
        return new CardState(Deck.of(discardedCards, rng), faceUpCards(), newDiscardedBag);
    }

    /**
     * Returns this card state but with the given cards added to the discards
     *
     * @param additionalDiscards more discarded cards to be added
     * @return this card state but with the given cards added to the discards
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(deckOfCards, faceUpCards(), discardedCards.union(additionalDiscards));
    }
}
