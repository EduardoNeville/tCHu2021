package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.sql.Array;
import java.sql.SQLOutput;
import java.util.*;

public class GameTest {

    @Test
    public void game(){
        Map<PlayerId, Player> playerMap = new HashMap<>(){{
//            put(PlayerId.PLAYER_1, new TestPlayer(12980312, ChMap.routes()));
//            put(PlayerId.PLAYER_2, new TestPlayer(43213213, ChMap.routes()));
            put(PlayerId.PLAYER_1, new TestPlayer(123, ChMap.routes()));
            put(PlayerId.PLAYER_2, new TestPlayer(1, ChMap.routes()));
        }};

        Map<PlayerId, String> playerNames = new HashMap<>(){{
            put(PlayerId.PLAYER_1, "Joueur 1");
            put(PlayerId.PLAYER_2, "Joueur 2");
        }};





        Game.play(playerMap, playerNames, SortedBag.of(ChMap.tickets()), new Random());
    }




    private static class TestPlayer implements Player {
        private static final int TURN_LIMIT = 1000;

        private final Random rng;
        // Toutes les routes de la carte
        private final ArrayList<Route> allRoutes;

        private int turnCounter;
        private PlayerState ownState;
        private PublicGameState gameState;
        private PlayerId ownId = null;
        private PlayerId otherId = null;
        SortedBag<Ticket> initTickets;

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;

        public TestPlayer(long randomSeed, List<Route> allRoutes) {
            this.rng = new Random(randomSeed);
            this.allRoutes = new ArrayList<>(ChMap.routes());
            this.turnCounter = 0;
        }

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
            this.ownId = ownId;
            for(PlayerId id: playerNames.keySet()){
                if (id!=ownId)
                    otherId = id;
            }
            System.out.println("inti Players : own = " + ownId + " players names Map = " + playerNames);
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            this.initTickets = tickets;
            System.out.println("Init ticekts: " + tickets);
        }

        @Override
        public void receiveInfo(String info) {
            System.out.println("I (" + ownId + ") received info :   " + info);
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.gameState = newState;
            this.ownState = ownState;
        }

        @Override
        public void selfInitialTicketChoice(SortedBag<Ticket> tickets) {
            //this.initTickets = tickets;
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            Set<Ticket> chosen = new HashSet<>();
            for (int i = 0; i < 3; i++) {
                chosen.add(initTickets.get(i));
            };
            return SortedBag.of(chosen);
        }

        private List<Route> claimableRoutes() {
            List<Route> claimable = new ArrayList<>();
            for (Route r : allRoutes) {
                if (ownState.canClaimRoute(r)){
                    claimable.add(r);
                }
            }

            List<Route> finalClaimable = new ArrayList<>();

            List<Route> enemyRoutes = gameState.playerState(otherId).routes();
            for (Route route: claimable){
                if(!enemyRoutes.contains(route))
                    finalClaimable.add(route);

            }

            return claimable;
        }

        @Override
        public TurnKind nextTurn() {

            turnCounter += 1;
            if (turnCounter > TURN_LIMIT)
                throw new Error("Trop de tours joués !");

            // Détermine les routes dont ce joueur peut s'emparer
            List<Route> claimableRoutes = claimableRoutes();
            System.out.println( "Route claimables =" + claimableRoutes);
            System.out.println("Cars de " + ownId + " " + ownState.carCount() + "routes aquises : " + ownState.routes().size());
            if (claimableRoutes.isEmpty() && !gameState.cardState().isDeckEmpty()) {
                return TurnKind.DRAW_CARDS;
            } else if(!claimableRoutes.isEmpty()) {
                int routeIndex = rng.nextInt(claimableRoutes.size());
                Route route = claimableRoutes.get(routeIndex);
                List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

                routeToClaim = route;
                initialClaimCards = cards.get(0);
                return TurnKind.CLAIM_ROUTE;
            }
            else{
                return TurnKind.DRAW_TICKETS;
            }
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            SortedBag<Ticket> chosen = SortedBag.of();
            for (int i = 0; i < rng.nextInt(options.size()); i++) {
                options.get(i);
            };
            return chosen;
        }

        @Override
        public int drawSlot() {
            int i =0;
            for(Card c:gameState.cardState().faceUpCards()){
                if(c != null)
                    return i;
                i++;
            }
            //return rng.nextInt(Constants.FACE_UP_CARDS_COUNT);
            return 6;
        }

        @Override
        public Route claimedRoute() {
            allRoutes.remove(routeToClaim);
            return routeToClaim;
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            return initialClaimCards;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            return options.get(0);
        }
    }
}
