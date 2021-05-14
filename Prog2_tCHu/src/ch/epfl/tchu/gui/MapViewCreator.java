package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
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
        void chooseCards(List<SortedBag<Card>> options,ChooseCardsHandler handler);
    }

    private Group caseGroup(){

        Circle circle1Wagon = new Circle();
        Circle circle2Wagon = new Circle();
        Rectangle rectangleWagon = new Rectangle();
        rectangleWagon.getStyleClass().add("filled");

        Group wagonGroup = new Group(circle1Wagon,circle2Wagon,rectangleWagon);
        wagonGroup.getStyleClass().addAll("car");

        Rectangle caseRectangle = new Rectangle();
        caseRectangle.getStyleClass().addAll("track", "filled");

        Group caseGroup = new Group(caseRectangle,wagonGroup);
        caseGroup.setId("AT1_STG_1_1");
        return caseGroup;
    }

    private Group routeGroup(){
        Group routeGroup = new Group(caseGroup());
        routeGroup.getStyleClass().addAll("route","UNDERGROUN","NEUTRAL");
        routeGroup.setId("AT1_STG_1");
        return routeGroup;
    }

    public ObservableGameState createMapView(ObjectProperty<ClaimRouteHandler> claimRouteHandlerObjectProperty,
                                             CardChooser cardChooser){
        ImageView imageView = new ImageView();
        Pane mapView = new Pane(routeGroup(),imageView);
        mapView.getStylesheets().addAll("map.css","colors.css");


        return
    }

}
