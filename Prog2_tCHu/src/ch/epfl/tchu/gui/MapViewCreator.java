package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;
import javafx.beans.property.ObjectProperty;
import ch.epfl.tchu.gui.ActionHandler.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
        void chooseCards(List<SortedBag<Card>> options,ChooseCardsHandler handler);
    }

    //TODO is this correct?
    private static Group caseGroup(){

        Circle circle1Wagon = new Circle(3);
        circle1Wagon.setCenterX(12);
        circle1Wagon.setCenterY(6);
        Circle circle2Wagon = new Circle(3);
        circle2Wagon.setCenterX(24);
        circle2Wagon.setCenterY(6);

        Rectangle rectangleWagon = new Rectangle();
        rectangleWagon.getStyleClass().add("filled");

        Group wagonGroup = new Group(circle1Wagon,circle2Wagon,rectangleWagon);
        wagonGroup.getStyleClass().addAll("car");

        Rectangle voieRectangle = new Rectangle();
        voieRectangle.getStyleClass().addAll("track", "filled");
        voieRectangle.setWidth(36d);
        voieRectangle.setHeight(12d);

        return new Group(voieRectangle, wagonGroup);
    }

    private static Group routeGroup(Route route){
        Group routeGroup = new Group();
        routeGroup.getStyleClass().addAll("route",
                                            route.level().name(),
                                            route.color() == null ? "NEUTRAL" : route.color().name());
        routeGroup.setId(route.id());
        for(int i = 1 ; i<= route.length(); i++){
            Group box = caseGroup();
            routeGroup.getChildren().add(box);
            box.setId(routeGroup.getId() + "_" + i);
        }
        return routeGroup;
    }

    //TODO are these the correct attributes?
    public static Node createMapView(ObservableGameState observableGameState,
                                     ObjectProperty<ClaimRouteHandler> claimRouteHandlerObjectProperty,
                                     CardChooser cardChooser){
        ImageView imageView = new ImageView();
        //TODO what route to call here if needed?

        Pane mapView = new Pane(imageView);
        mapView.getStylesheets().addAll("map.css","colors.css");
        ChMap.routes().forEach(r -> mapView.getChildren().add(routeGroup(r)));
//        mapView.getChildren().addAll();
//        mapView.getChildren().addAll(routeGroup().getChildren().sorted());

        //TODO how to implement nodes into observable...
//        ObservableGameState observableGameState = new ObservableGameState();

        return mapView;
    }

}
