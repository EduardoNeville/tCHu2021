package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * A partition of stations.
 *
 * @author Martin Sanchez Lopez (313238)
 */
public final class StationPartition implements StationConnectivity {

    //index is station id, value at index is it's representative
    private final int[] stationsPartitionsArray;

    /**
     * Returns true if the two stations are connected, flase otherwise.
     *
     * @param s1 Station 1
     * @param s2 Station 2
     * @return true if the two stations are connected, flase otherwise
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        int id1 = s1.id();
        int id2 = s2.id();

        //station outside the array's bounds
        if (id1 >= stationsPartitionsArray.length || id2 >= stationsPartitionsArray.length) {
            return id1 == id2;
        }

        return representative(id1) == representative(id2);
    }

    /**
     * Returns the representative of the given station id.
     *
     * @param stationId id of station
     * @return the representative of the given station id
     */
    private int representative(int stationId) {
        return stationsPartitionsArray[stationId];
    }

    /**
     * Constructor
     *
     * @param partition partition
     */
    private StationPartition(int[] partition) {
        int[] tempArray = new int[partition.length];
        System.arraycopy(partition, 0, tempArray, 0, partition.length);
        this.stationsPartitionsArray = tempArray;
    }

    /**
     * A StationPartition builder.
     */
    public final static class Builder {

        private int[] partitionArray;

        /**
         * Constructs a StationPartition builder.
         *
         * @param stationCount biggest station id + 1
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            partitionArray = new int[stationCount];
            for (int i = 0; i < stationCount; i++) {
                partitionArray[i] = i;
            }

        }

        /**
         * Connects two stations by putting them in the same partition.
         *
         * @param s1 station 1
         * @param s2 station 2
         * @return this Builder
         */
        public Builder connect(Station s1, Station s2) {
            int id1 = s1.id();
            int id2 = s2.id();


            //Both stations are they own representative
            if (representative(id1) == id1 && representative(id2) == id2) {
                //station1 becomes representative by default
                assignRepresentative(id2, id1);
            }

            //Both station already have a representative
            else if (representative(id1) != id1 && representative(id2) != id2) {
                //connect the two 'branches'
                assignRepresentative(getHighestRepresentative(id2), getHighestRepresentative(id1));
            }

            //One station already has another representative
            else if (representative(id1) != id1) {
                assignRepresentative(id2, getHighestRepresentative(id1));
            } else if (representative(id2) != id2) {
                assignRepresentative(id1, getHighestRepresentative(id2));
            }
            return this;
        }

        /**
         * Returns the representative of the given station id.
         *
         * @param stationId id of station
         * @return the representative of the given station id
         */
        private int representative(int stationId) {
            return partitionArray[stationId];
        }

        /**
         * Returns the highest (self pointing) representative of this station's partition.
         *
         * @param stationId id of station
         * @return the highest (self pointing) representative of this station's partition.
         */
        private int getHighestRepresentative(int stationId) {
            int id = representative(stationId);
            while (id != representative(id)) {
                id = representative(id);
            }
            return id;
        }

        /**
         * Assigns representative to given station.
         *
         * @param stationId        id of station to assign
         * @param representativeId id or representative id
         */
        private void assignRepresentative(int stationId, int representativeId) {
            partitionArray[stationId] = representativeId;
        }

        /**
         * Flattens partition so that every station id either points to the trail's representative
         * or itself it is the trails representative
         */
        private void flattenPartition() {
            for (int i = 0; i < partitionArray.length; i++) {
                assignRepresentative(i, getHighestRepresentative(i));
            }
        }

        /**
         * Returns a StationPartition with the builder's current station partition.
         *
         * @return a StationPartition with the station partition of <code>this</code>
         */
        public StationPartition build() {
            flattenPartition();
            return new StationPartition(partitionArray);
        }
    }

}
