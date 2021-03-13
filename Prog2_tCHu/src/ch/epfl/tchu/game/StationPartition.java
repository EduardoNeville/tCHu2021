package ch.epfl.tchu.game;

public final class StationPartition implements StationConnectivity {

    @Override
    public boolean connected(Station s1, Station s2) {
        return false;//change?
    }

    private StationPartition(){

    }

    public class Builder{


        public Builder(int stationCount){

        }

        public Builder connect(Station s1, Station s2){

        }

        public StationPartition build(){

        }
    }

}
