package datastructure;

import datastructure.exceptions.NodeNotExistException;
import model.Main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * precondition:
 *     no isolate node exists;
 *         (guaranteed by contract valid path consists of at least 2 nodes)
 */
public class Graph<T extends Node<?>> {
    private final int capacity;
    private final LinkedList<Integer> vacantIds = new LinkedList<>();
    private final HashMap<T, Integer> n2id;
    private final HashMap<Integer, T> id2n;
    private int[][] distance;
    
    public Graph(int capacity) {
        this.capacity = capacity;
        this.n2id = new HashMap<>(capacity);
        this.id2n = new HashMap<>(capacity);
        initDistance();
        for (int i = 0; i < capacity; i++) {
            vacantIds.add(i);
        }
    }
    
    protected int getCapacity() {
        return capacity;
    }
    
    public int size() {
        return n2id.size();
    }
    
    protected void initDistance() {
        distance = new int[capacity][capacity];
        for (Node<?> node : n2id.keySet()) {
        
        }
    }
    
    protected int getNodeId(T node) throws NodeNotExistException {
        Integer result = n2id.get(node);
        if (result == null) {
            throw new NodeNotExistException(node);
        } else {
            return result;
        }
    }
    
    protected int createAndGetNodeId(T node) {
        Integer result = n2id.get(node);
        if (result == null) {
            result = vacantIds.removeFirst();
            n2id.put(node, result);
            distance[result][result] = 0;
        }
        return result;
    }
    
    public boolean containsNode(T node) {
        return n2id.get(node) != null;
    }
    
    public boolean containsEdge(T start, T end) {
        Integer firstId = n2id.get(start);
        Integer secondId = n2id.get(end);
        if (firstId == null || secondId == null) {
            return false;
        }
        assert !Main.DEBUG
                || access[firstId][secondId] == access[secondId][firstId];
        return access[firstId][secondId] != 0;
    }
    
    public void addEdge(T start, T end) {
        int firstId = createAndGetNodeId(start);
        int secondId = createAndGetNodeId(end);
        access[firstId][secondId]++;
        access[secondId][firstId]++;
    }
    
    public void removeEdge(T start, T end) {
        int firstId = n2id.get(start);
        int secondId = n2id.get(end);
        access[firstId][secondId]--;
        access[secondId][firstId]--;
        if (Main.DEBUG) {
            assert access[firstId][secondId] >= 0;
            assert access[secondId][firstId] >= 0;
        }
    }
    
    public boolean removeIfIsolated(T node) {
        int id = n2id.get(node);
        for (int i = 0; i < capacity; i++) {
            if (access[i][id] + access[id][i] != 0) {
                return false;
            }
        }
        n2id.remove(node);
        vacantIds.addFirst(id);
        distance[id][id] = -1;
        return true;
    }
    
    public int bfs(T start, T end) throws NodeNotExistException {
        int startId = getNodeId(start);
        int endId = getNodeId(end);
        assert !Main.DEBUG
                || startId == endId || distance[startId][endId] == 0;
        if (distance[startId][endId] != -1) {
            return distance[startId][endId];
        }
        HashSet<Integer> visited = new HashSet<>();
        HashSet<Integer> currentlyVisiting = new HashSet<>();
        HashSet<Integer> toVisit = new HashSet<>();
        visited.add(startId);
        currentlyVisiting.add(startId);
        for (int length = 1; true; length++) {
            if (currentlyVisiting.size() == 0) {
                return Integer.MAX_VALUE;
            }
            for (int id : currentlyVisiting) {
                for (int validId : n2id.values()) {
                    assert !Main.DEBUG
                            || access[id][validId] == access[validId][id];
                    if (access[id][validId] == 0 // not accessible
                            || id == validId // self
                            || visited.contains(validId)) { // already visited
                        continue;
                    }
                    distance[startId][validId] = length;
                    distance[validId][startId] = length;
                    if (validId == endId) {
                        return length;
                    }
                    toVisit.add(validId);
                }
            }
            currentlyVisiting = toVisit;
            visited.addAll(currentlyVisiting);
            toVisit = new HashSet<>();
        }
    }
}
