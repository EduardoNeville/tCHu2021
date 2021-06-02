package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * GameState class
 * Methods: playerState, currentPlayerState, topTickets, withoutTopTickets, topCard,
 * withoutTopCard, withMoreDiscardedCards, withCardsDeckRecreatedIfNeeded, withInitiallyChosenTickets,
 * withChosenAdditionalTickets, withDrawnFaceUpCard, withBlindlyDrawnCard, withClaimedRoute, lastTurnBegins,
 * forNextTurn
 *
 * @author Eduardo Neville (314667)
 */
public final class GameState extends PublicGameState {

    private final Deck<Ticket> tickets;
    private final Map<PlayerId, PlayerState> playerState;
    private final CardState privateCardState;

    private GameState(Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer,
                      Deck<Ticket> ticketsDeck, CardState privateCardState, PlayerId currentPlayerId) {
        super(ticketsDeck.size(), privateCardState, currentPlayerId,
                makePublic(playerState), lastPlayer);

        this.tickets = ticketsDeck;
        this.playerState = playerState;
        this.privateCardState = privateCardState;
    }

    /**
     * Initial static constructor that initializes the game
     *
     * @param tickets tickets in the deck
     * @param rng     used to shuffle the tickets
     * @return The initial GameState at the beginning of the match
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {

        SortedBag<Card> withoutInitialCards = SortedBag.of(Constants.ALL_CARDS);
        List<Card> shuffleDeck = withoutInitialCards.toList();
        Collections.shuffle(shuffleDeck);
        Deck<Card> theDeck = Deck.of(withoutInitialCards, rng);

        Map<PlayerId, PlayerState> initialMap = new HashMap<>();
        initialMap.put(PlayerId.PLAYER_1, PlayerState.initial(theDeck.topCards(4)));
        theDeck = theDeck.withoutTopCards(4);
        initialMap.put(PlayerId.PLAYER_2, PlayerState.initial(theDeck.topCards(4)));
        theDeck = theDeck.withoutTopCards(4);

        List<PlayerId> listOfPlayers = new ArrayList<>();
        listOfPlayers.add(PlayerId.PLAYER_1);
        listOfPlayers.add(PlayerId.PLAYER_2);
        Random random = new Random();

        PlayerId randStartingPlayer = listOfPlayers.get(random.nextInt(listOfPlayers.size()));

        CardState privateCardState = CardState.of(theDeck);

        return new GameState(initialMap, null, Deck.of(tickets, rng),
                privateCardState, randStartingPlayer);
    }

    private static Map<PlayerId, PublicPlayerState> makePublic(Map<PlayerId, PlayerState> playerStateMap) {

        Map<PlayerId, PlayerState> playerStateTreeMap = new TreeMap<>(playerStateMap);

        PlayerState playerState1 = playerStateTreeMap.get(PlayerId.PLAYER_1);
        PlayerState playerState2 = playerStateTreeMap.get(PlayerId.PLAYER_2);

        PublicPlayerState publicPlayerState1 = new PublicPlayerState(playerState1.tickets().size(),
                playerState1.cards().size(), playerState1.routes());
        PublicPlayerState publicPlayerState2 = new PublicPlayerState(playerState2.tickets().size(),
                playerState2.cards().size(), playerState2.routes());

        Map<PlayerId, PublicPlayerState> publicPlayerStateMap = new TreeMap<>();
        publicPlayerStateMap.put(PlayerId.PLAYER_1, publicPlayerState1);
        publicPlayerStateMap.put(PlayerId.PLAYER_2, publicPlayerState2);

        return publicPlayerStateMap;
    }

    /**
     * Returns the full playerState
     *
     * @param playerId Player in question
     * @return full playerState of playerId
     */
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * Returns the full playerState of the currentPlayerId
     *
     * @return full playerState of currentplayerId
     */
    public PlayerState currentPlayerState() {
        return playerState(currentPlayerId());
    }

    /**
     * Returns the toptickets of the deck
     *
     * @param count # of tickets returned
     * @return # of tickets at the top that are being returned
     * @throws IllegalArgumentException thrown if count is smaller than 0 or tickets is smaller than count
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(count > 0 || tickets.size() > count);

        return tickets.topCards(count);
    }

    /**
     * Returns the deck without the toptickets of the deck
     *
     * @param count # of tickets that we remove
     * @return deck without the toptickets
     * @throws IllegalArgumentException thrown if count is smaller than 0 or tickets is smaller than count
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count > 0 || tickets.size() > count);
        return new GameState(playerState, lastPlayer(),
                tickets.withoutTopCards(count),
                privateCardState, currentPlayerId());
    }

    /**
     * Returns the top card of the deck
     *
     * @return top card of the deck
     * @throws IllegalArgumentException thrown if privateCardState is an empty deck
     */
    public Card topCard() {
        Preconditions.checkArgument(!privateCardState.isDeckEmpty());
        return privateCardState.topDeckCard();
    }

    /**
     * Returns the deck without the top card
     *
     * @return Returns the deck without the top card
     * @throws IllegalArgumentException thrown if privateCardState is an empty deck
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(!privateCardState.isDeckEmpty());
        return new GameState(playerState,
                lastPlayer(), tickets,
                privateCardState.withoutTopDeckCard(),
                currentPlayerId());
    }

    /**
     * Returns the Gamestate with more discardedCards added to the deck
     *
     * @param discardedCards discardedCards in question
     * @return Gamestate with more discardedCards
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(playerState,
                lastPlayer(),
                tickets,
                privateCardState.withMoreDiscardedCards(discardedCards),
                currentPlayerId());
    }

    /**
     * Returns a deck of cards from discarded if deck of cards is empty
     *
     * @param rng random var needed to shuffle the cards
     * @return new deck of cards from shuffled discarded cards or the same gamestate
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        if (privateCardState.isDeckEmpty()) {

            return new GameState(playerState, lastPlayer(),
                    tickets, privateCardState.withDeckRecreatedFromDiscards(rng),
                    currentPlayerId());
        } else return this;
    }

    /**
     * Added tickets to a player
     *
     * @param playerId      player in question
     * @param chosenTickets tickets added
     * @return new GameState with added tickets to a player
     * @throws IllegalArgumentException thrown if the players ticket size is greater than 0
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState(playerId).tickets().size() < 1);
        Map<PlayerId, PlayerState> addedTickets = new TreeMap<>(playerState);
        addedTickets.put(playerId, playerState(playerId).withAddedTickets(chosenTickets));
        return new GameState(addedTickets, lastPlayer(),
                tickets, privateCardState,
                currentPlayerId());
    }

    //Block 2 of methods

    /**
     * Removed drawnTickets and added the chosenTickets to the player
     *
     * @param drawnTickets  removed tickets
     * @param chosenTickets added tickets
     * @return gameState with removed drawnTickets and added the chosenTickets
     * @throws IllegalArgumentException thrown if drawnTicket doesn't contain chosenTickets
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));

        Map<PlayerId, PlayerState> additionalTickets = new TreeMap<>(playerState);
        additionalTickets.put(currentPlayerId(), playerState(currentPlayerId()).withAddedTickets(chosenTickets));
        return new GameState(additionalTickets, lastPlayer(),
                tickets.withoutTopCards(drawnTickets.size()), privateCardState,
                currentPlayerId());
    }

    /**
     * Returns GameState with playerState changed to fit the card and privatecardState changed
     *
     * @param slot position of card swap
     * @return GameState with playerState changed to fit the card and privatecardState changed
     * @throws IllegalArgumentException thrown if we cannot draw cards
     */
    public GameState withDrawnFaceUpCard(int slot) {
        var playerstatecopy = new HashMap<>(playerState);
        playerstatecopy.put(currentPlayerId(), playerState(currentPlayerId())
                .withAddedCards(SortedBag.of(privateCardState.faceUpCard(slot))));

        return new GameState(playerstatecopy, lastPlayer(), tickets,
                privateCardState.withDrawnFaceUpCard(slot),
                currentPlayerId());
    }

    /**
     * Picked Card from deck to player
     *
     * @return player has a new card
     * @throws IllegalArgumentException thrown if we cannot draw cards
     */
    public GameState withBlindlyDrawnCard() {

        Map<PlayerId, PlayerState> drawnCards = new TreeMap<>(playerState);
        drawnCards.put(currentPlayerId(), currentPlayerState().withAddedCards(SortedBag.of(topCard())));

        return new GameState(drawnCards, lastPlayer(), tickets, privateCardState.withoutTopDeckCard(), currentPlayerId());
    }

    /**
     * Claimed route
     *
     * @param route route in question
     * @param cards cards used to claim the route
     * @return player has new route and dicards has new cards
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        Map<PlayerId, PlayerState> withClaimedRoute = new TreeMap<>(playerState);
        withClaimedRoute.put(currentPlayerId(), currentPlayerState().withClaimedRoute(route, cards));

        return new GameState(withClaimedRoute, lastPlayer(), tickets,
                privateCardState.withMoreDiscardedCards(cards), currentPlayerId());
    }


    /**
     * Asks were the last turn will begin
     *
     * @return will the last turn begin
     */
    public boolean lastTurnBegins() {
        return lastPlayer() == null && currentPlayerState().carCount() <= 2;
    }

    /**
     * Makes last player begin
     *
     * @return returns player that begins?
     */
    public GameState forNextTurn() {
        PlayerId lastPlayer1 = lastTurnBegins()
                ? currentPlayerId()
                : lastPlayer();

        return new GameState(playerState, lastPlayer1, tickets,
                privateCardState, currentPlayerId().next());
    }

    public GameState withTradeDealMade(TradeDeal deal, PlayerId offered, PlayerId acceptor) {
        Map<PlayerId, PlayerState> newMap = new TreeMap<>();
        newMap.put(offered, playerState(offered).withTradeDealMade(deal, true));
        newMap.put(acceptor, playerState(acceptor).withTradeDealMade(deal, false));

        return new GameState(newMap, lastPlayer(), tickets, privateCardState, currentPlayerId());
    }


}
