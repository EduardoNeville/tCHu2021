package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * CardState Class
 * Methods: withDrawnFaceUpCard, topDeckCard, withoutTopDeckCard, withDeckRecreatedFromDiscards, withMoreDiscardedCards
 * Constructor:
 * @author Eduardo Neville
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
     * @param deck Deck we want to find the 5 cards
     * @return first 5 cards
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() > 4);
        List<Card> faceUp = new ArrayList<>();
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; ++i) { //change 5 to constant value
            faceUp.add(deck.topCard());
            deck = deck.withoutTopCard();
        }
        return new CardState(deck, faceUp, SortedBag.of());
    }
    //Listof(first 5 cards) use method faceUpCard

    /**
     * @param slot
     * @return
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, 5);
        Preconditions.checkArgument(!deckofCards.isEmpty());
        List<Card> faceUpCards = new ArrayList<>(faceUpCards());
        faceUpCards.remove(slot);
        faceUpCards.add(slot, deckofCards.topCard());
        return new CardState(deckofCards.withoutTopCard(), faceUpCards, discardedCards);
    }

    /**
     * Method returns the top card of the deck
     *
     * @return Top card of the deck
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!deckofCards.isEmpty());
        return deckofCards.topCard();
    }

    /**
     * @return
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!deckofCards.isEmpty());
        return new CardState(this.deckofCards.withoutTopCard(), faceUpCards(), discardedCards);
    }

    /**
     * @param rng
     * @return
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(deckofCards.isEmpty());
        var shuffledCards = Deck.of(discardedCards, rng);

        return new CardState(shuffledCards, faceUpCards(), discardedCards);
    }

    /**
     * @param additionalDiscards
     * @return
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(deckofCards, faceUpCards(), discardedCards.union(additionalDiscards));
    }
}
