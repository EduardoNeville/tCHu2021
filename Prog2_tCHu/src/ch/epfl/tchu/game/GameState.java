package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

public final class GameState extends PublicGameState{
    private final Deck<Ticket> tickets;
    private final Map playerState;
    private final PlayerId lastPlayer,currentPlayer;
    private final PublicCardState publicCardState;
    private final Deck<Card> deckofCards;
    private final CardState privateCardState;


    private GameState(int ticketCounts, PublicCardState publicCardState,
                     PlayerId currentPLayer, Map playerState, PlayerId lastPlayer,
                     Deck<Ticket> tickets, Deck<Card> deckofCards, CardState privateCardState){
        super(ticketCounts,publicCardState,currentPLayer,
                makePublic(playerState),lastPlayer);

        this.tickets = tickets;
        this.playerState = playerState;
        this.lastPlayer = lastPlayer; // Make immutable
        this.currentPlayer = currentPLayer; // Make immutable
        this.publicCardState = publicCardState; // Make immutable
        this.deckofCards = deckofCards;
        this.privateCardState = privateCardState;
    }

    public static GameState initial(SortedBag<Ticket> tickets, Random rng){

        SortedBag<Card> withoutInitialCards = SortedBag.of(Constants.ALL_CARDS);
        List<Card> shuffleDeck = withoutInitialCards.toList();
        Collections.shuffle(shuffleDeck);
        Deck theDeck = Deck.of(withoutInitialCards,rng);

        Map<PlayerId,PlayerState> initialMap = new HashMap<>();
        initialMap.put(PlayerId.PLAYER_1, PlayerState.initial(theDeck.topCards(4))); //TODO
        theDeck = theDeck.withoutTopCards(4);
        initialMap.put(PlayerId.PLAYER_2, PlayerState.initial(theDeck.topCards(4))); //TODO
        theDeck = theDeck.withoutTopCards(4);

        List<PlayerId> listOfPlayers = new ArrayList<>();
        listOfPlayers.add(PlayerId.PLAYER_1);
        listOfPlayers.add(PlayerId.PLAYER_2);
        Random random = new Random();
        PlayerId randStartingPlayer = listOfPlayers.get(random.nextInt());

        int ticketCount = tickets.size();
        PublicCardState initialCardState = new PublicCardState(new ArrayList<>(),theDeck.size(),0);

        //TODO
        GameState initialGameState = new GameState(ticketCount, initialCardState,
                randStartingPlayer,initialMap,null,tickets,
                theDeck,);


        return initialGameState;
    }


    private static Map<PlayerId, PublicPlayerState> makePublic(Map<PlayerId,PlayerState> playerStateMap){
        int cardCount1, cardCount2;
        int ticketCount1, ticketCount2;
        List<Route> routes1,routes2;

        Map<PlayerId, PlayerState> playerStateTreeMap = new TreeMap<PlayerId, PlayerState>(playerStateMap);

        PlayerState playerState1 = playerStateTreeMap.get(PlayerId.PLAYER_1);
        PlayerState playerState2 = playerStateTreeMap.get(PlayerId.PLAYER_2);

        cardCount1 = playerState1.cards().size();
        ticketCount1 = playerState1.tickets().size();
        routes1 = playerState1.routes();

        cardCount2 = playerState2.cards().size();
        ticketCount2 = playerState2.tickets().size();
        routes2 = playerState2.routes();

        PublicPlayerState publicPlayerState1 = new PublicPlayerState(ticketCount1,cardCount1,routes1);
        PublicPlayerState publicPlayerState2 = new PublicPlayerState(ticketCount2,cardCount2,routes2);

        Map<PlayerId, PublicPlayerState> publicPlayerStateMap = new TreeMap<PlayerId, PublicPlayerState>();
        publicPlayerStateMap.put(PlayerId.PLAYER_1,publicPlayerState1);
        publicPlayerStateMap.put(PlayerId.PLAYER_2,publicPlayerState2);

        return publicPlayerStateMap;
    }

    public PlayerState playerState(PlayerId playerId){
        //override method from parent class
    }

    public PlayerState currentPlayerState(){
        //override method from parent class
    }


    //Bloc 1 of methods


    public SortedBag<Ticket> topTickets(int count){
        Preconditions.checkArgument(count>0 || tickets.size()>count);
        return tickets.topCards(count);
    }

    //TODO
    public GameState withoutTopTickets(int count){
        Preconditions.checkArgument(count>0 || tickets.size()>count);
        return new GameState(); //take out toptickets from gamestate deck using constructor
    }

    public Card topCard(){
        Preconditions.checkArgument(!deckofCards.isEmpty());
        return deckofCards.topCard();
    }

    public GameState withoutTopCard(){
        Preconditions.checkArgument(!deckofCards.isEmpty());
        //take out top cards from gamestate deck using constructor
    }

    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards){
        //add cards to the Gamestate discard pile
    }

    public GameState withCardsDeckRecreatedIfNeeded(Random rng){
        if (deckofCards.isEmpty()){
            //CardState thenewCardState = new CardState(Deck.of(discardedCards, rng),, discardedCards);
        }
    }

    //TODO to fix constructor first
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets){
        Preconditions.checkArgument(playerState(playerId).tickets().size()<1);
        return new GameState();
    }

    //Block 2 of methods

    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets){
        Preconditions.checkArgument(!drawnTickets.contains(chosenTickets));

    }

    public GameState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(!canDrawCards());


    }

    public GameState withBlindlyDrawnCard(){
        Preconditions.checkArgument(!canDrawCards());

    }

    public GameState withClaimedRoute(Route route, SortedBag<Card> cards){

    }

    //Block 3 of methods

    public boolean lastTurnBegins(){

    }

    public GameState forNextTurn(){

    }


}
