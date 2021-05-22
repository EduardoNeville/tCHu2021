package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import ch.epfl.tchu.gui.ActionHandler.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * Class MapViewCreator
 *
 * @author Eduardo Neville (314667)
 */
class MapViewCreator {

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }

    private static Group caseGroup() {

        Circle circle1Wagon = new Circle(3);
        circle1Wagon.setCenterX(12);
        circle1Wagon.setCenterY(6);
        Circle circle2Wagon = new Circle(3);
        circle2Wagon.setCenterX(24);
        circle2Wagon.setCenterY(6);

        Rectangle rectangleWagon = new Rectangle();
        rectangleWagon.getStyleClass().add("filled");

        Group wagonGroup = new Group(circle1Wagon, circle2Wagon, rectangleWagon);
        wagonGroup.getStyleClass().addAll("car");

        Rectangle rectanglePath = new Rectangle();
        rectanglePath.getStyleClass().addAll("track", "filled");
        rectanglePath.setWidth(36d);
        rectanglePath.setHeight(12d);

        return new Group(rectanglePath, wagonGroup);
    }

    private static Group routeGroup(Route route) {
        Group routeGroup = new Group();
        routeGroup.getStyleClass().addAll("route",
                route.level().name(),
                route.color() == null ? "NEUTRAL" : route.color().name());
        routeGroup.setId(route.id());
        for (int i = 1; i <= route.length(); i++) {
            Group box = caseGroup();
            routeGroup.getChildren().add(box);
            box.setId(routeGroup.getId() + "_" + i);
        }
        return routeGroup;
    }

    public static Node createMapView(ObservableGameState observableGameState,
                                     ObjectProperty<ClaimRouteHandler> claimRouteHandlerObjectProperty,
                                     CardChooser cardChooser) {

        ImageView imageView = new ImageView();
        Pane mapView = new Pane(imageView);
        mapView.getStylesheets().addAll("map.css", "colors.css");

        //add routes to the pane and set their activation conditions and listeners
        ChMap.routes().forEach(r -> {
            Group routeGroup = routeGroup(r);

            mapView.getChildren().add(routeGroup);
            //add class if route gets an owner
            observableGameState.getRouteOwners(r).addListener((o, oV, nV)-> {
                if (o.getValue() != null)
                    routeGroup.getStyleClass().add(o.getValue().name());
            });

            routeGroup.disableProperty().bind(
                    claimRouteHandlerObjectProperty.isNull()
                            .or(observableGameState.getPlayerClaimableRoute(r).not()));

            routeGroup.setOnMouseClicked(e -> {
                List<SortedBag<Card>> possibleClaimCards = observableGameState.possibleClaimCards(r);
                ClaimRouteHandler claimRouteH = claimRouteHandlerObjectProperty.getValue();

                ChooseCardsHandler chooseCardsH =
                        chosenCards -> claimRouteH.onClaimRoute(r, chosenCards);
                cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
            });

        });

        return mapView;
    }

}
