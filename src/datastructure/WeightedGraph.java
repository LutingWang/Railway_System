package datastructure;

import datastructure.exceptions.NodeNotExistException;
import model.Main;

public class WeightedGraph<T extends Node<?>> extends Graph<T> {
    private final int[][] adj;
    private int[][] distance;
    
    public WeightedGraph(int capacity) {
        super(capacity);
        adj = new int[capacity][capacity];
        for (int i = 0; i < capacity; i++) {
            for (int j = 0; j < capacity; j++) {
                adj[i][j] = Integer.MAX_VALUE;
            }
        }
    }
    
    @Override
    protected void initDistance() {
        int capacity = super.getCapacity();
        distance = new int[capacity][capacity];
        for (int i = 0; i < capacity; i++) {
            for (int j = 0; j < capacity; j++) {
                if (adj[i][j] != 0) {
                    distance[i][j] = adj[i][j]; // contains edge
                } else {
                    distance[i][j] = -1; // did not run
                }
            }
        }
    }
    
    public void addEdge(T start, T end, int weight) {
        super.addEdge(start, end);
        int firstId = super.createAndGetNodeId(start);
        int secondId = super.createAndGetNodeId(end);
        adj[firstId][secondId] = weight;
        adj[secondId][firstId] = weight;
    }
    
    @Override
    public void removeEdge(T start, T end) {
        super.removeEdge(start, end);
        
    }
    
    public int dijskstra(T start, T end) throws NodeNotExistException {
        Integer startId = super.getNodeId(start);
        Integer endId = super.getNodeId(end);
        assert !Main.DEBUG
                || !startId.equals(endId) || distance[startId][endId] == 0;
        if (distance[startId][endId] != -1) {
            return distance[startId][endId];
        }
        
    }
}
