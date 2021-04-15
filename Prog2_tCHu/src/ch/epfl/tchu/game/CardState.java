package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * CardState Class
 * Methods: withDrawnFaceUpCard, topDeckCard, withoutTopDeckCard, withDeckRecreatedFromDiscards, withMoreDiscardedCards
 * Constructor: CardState
 *
 * @author Eduardo Neville (314667)
 */
public class CardState extends PublicCardState {

    private final Deck<Card> deckofCards;
    private final SortedBag<Card> discardedCards;

    private CardState(Deck<Card> cardDeck, List<Card> faceUpCards, SortedBag<Card> discarded) {
        super(faceUpCards, cardDeck.size(), discarded.size());

        this.deckofCards = cardDeck;
        this.discardedCards = SortedBag.of(discarded);
    }

    /**
     * Static Initialization Bloc used to find first 5 cards
     *
     * @param deck
     *          Deck we want to find the 5 cards
     * @throws IllegalArgumentException
     *          If deck is smaller that 4
     * @return first 5 cards
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
     * @param slot
     *          the slot take by this new card
     * @throws IllegalArgumentException
     *          if slot isn't length 5
     * @return the new set of cards in the table
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, 5);
        Preconditions.checkArgument(!deckofCards.isEmpty());

        List<Card> newFaceUpCards = new ArrayList<>(faceUpCards());
        newFaceUpCards.remove(slot);
        newFaceUpCards.add(slot, deckofCards.topCard());
        return new CardState(deckofCards.withoutTopCard(),
                newFaceUpCards, discardedCards);
    }

    /**
     * Method returns the top card of the deck
     * @throws IllegalArgumentException
     *              if deckofCards is empty
     * @return Top card of the deck
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!deckofCards.isEmpty());
        return deckofCards.topCard();
    }

    /**
     * CardState without the top deck card
     * @throws IllegalArgumentException
     *              if deckofCards is empty
     * @return new cardstate without top card
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!deckofCards.isEmpty());
        return new CardState(this.deckofCards.withoutTopCard(), faceUpCards(), discardedCards);
    }

    /**
     * Reshuffling the discarded cards to make a new deck
     * @param rng
     *          random paramenter
     * @throws IllegalArgumentException
     *          if deckofCards is empty
     * @return new deck of shuffled cards made up of the previously discarded cards
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(deckofCards.isEmpty());
        SortedBag<Card> newDiscardedBag = SortedBag.of();
        return new CardState(Deck.of(discardedCards, rng), faceUpCards(), newDiscardedBag);
    }

    /**
     * Adding discarded cards to the discaerded pile
     * @param additionalDiscards
     *                      more discarded cards to be added
     * @return group of discarded cards
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(deckofCards, faceUpCards(), discardedCards.union(additionalDiscards));
    }
}
