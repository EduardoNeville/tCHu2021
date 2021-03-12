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
public class CardState extends PublicCardState{

    private final Deck<Card> deckofCards;
    private final SortedBag<Card> discardedCards;

    private CardState(Deck<Card> cardDeck, List<Card> faceUpCards, SortedBag<Card> discarded) {
        super(faceUpCards,cardDeck.size(),discarded.size());

        Preconditions.checkArgument(!cardDeck.isEmpty());
        Preconditions.checkArgument(!faceUpCards.isEmpty());

        this.deckofCards = cardDeck;
        this.discardedCards = SortedBag.of(discarded);
    }

    /**
     * Static Initialization Bloc used to find first 5 cards
     * @param deck Deck we want to find the 5 cards
     * @return first 5 cards
     */
    public static CardState of(Deck<Card> deck){
        Preconditions.checkArgument(deck.size()>4);
        //CardState cardState = new CardState();
        List<Card> faceUp = null;
        for (int i =0 ;i<5;++i){ //change 5 to constant value
            faceUp.add(deck.topCard());
            deck = deck.withoutTopCard();
        }
        return new CardState(deck,faceUp,SortedBag.of()); //add sortedbag with empty of for discarded
    }
    //Listof(first 5 cards) use method faceUpCard
    //rest are okay

    /**
     *
     * @param slot
     * @return
     */
    public CardState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(!deckofCards.isEmpty());
        Objects.checkIndex(slot, 5); //To be corrected
        faceUpCards.remove(slot);
        faceUpCards.add(slot,deckofCards.topCard());
        return new CardState(deckofCards,faceUpCards,discardedCards);
    }

    /**
     * Method returns the top card of the deck
     * @return Top card of the deck
     */
    public Card topDeckCard(){
        Preconditions.checkArgument(!deckofCards.isEmpty());
        return deckofCards.topCard(); //is this correct?
    }

    /**
     *
     * @return
     */
    public CardState withoutTopDeckCard(){
        Preconditions.checkArgument(!deckofCards.isEmpty());
        return new CardState(this.deckofCards.withoutTopCard(),faceUpCards,discardedCards);
        //is this correct?
    }

    /**
     *
     * @param rng
     * @return
     */
    public CardState withDeckRecreatedFromDiscards(Random rng){
        Preconditions.checkArgument(deckofCards.isEmpty());
        List<Card> tobeShuffled = discardedCards;
        Collections.shuffle(tobeShuffled,rng);
        Deck<Card> shuffledDeck= new Deck<Card>(tobeShuffled);

        return new CardState(shuffledDeck,faceUpCards,discardedCards);
    }

    /**
     *
     * @param additionalDiscards
     * @return
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
        var Discarded = SortedBag.of(discardedCards);
        Discarded.add(additionalDiscards);
        return new CardState(deckofCards,faceUpCards,)
    }
}
