package datastructure;

import datastructure.exceptions.NodeNotExistException;
import model.Main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * precondition:
 *     distinct nodes cannot exceed 250;
 *     no isolate node exists;
 *         (guaranteed by contract valid path consists of at least 2 nodes)
 *     all nodes are int;
 */
public class Graph {
    private static final int capacity = 250;
    
    private HashMap<Integer, Integer> n2id = new HashMap<>(capacity);
    private LinkedList<Integer> vacantIds = new LinkedList<>();
    private int[][] access = new int[capacity][capacity]; // entries non-neg
    private int[][] distance;
    
    public Graph() {
        initDistance();
        for (int i = 0; i < capacity; i++) {
            vacantIds.add(i);
            for (int j = 0; j < capacity; j++) {
                access[i][j] = 0;
            }
        }
    }
    
    public void initDistance() {
        distance = new int[capacity][capacity];
        for (int i = 0; i < capacity; i++) {
            for (int j = 0; j < capacity; j++) {
                if (i != j) {
                    if (access[i][j] != 0) {
                        distance[i][j] = 1; // contains edge
                    } else {
                        distance[i][j] = -1; // did not run
                    }
                }
            }
        }
    }
    
    private int getNodeId(int node) {
        Integer result = n2id.get(node);
        if (result == null) {
            result = vacantIds.removeFirst();
            n2id.put(node, result);
            distance[result][result] = 0;
        }
        return result;
    }
    
    public boolean containsNode(int node) {
        return n2id.get(node) != null;
    }
    
    public boolean containsEdge(int start, int end) {
        Integer firstId = n2id.get(start);
        Integer secondId = n2id.get(end);
        if (firstId == null || secondId == null) {
            return false;
        }
        assert !Main.DEBUG
                || access[firstId][secondId] == access[secondId][firstId];
        return access[firstId][secondId] != 0;
    }
    
    public void addEdge(int start, int end) {
        int firstId = getNodeId(start);
        int secondId = getNodeId(end);
        access[firstId][secondId]++;
        access[secondId][firstId]++;
    }
    
    public void removeEdge(int start, int end) {
        int firstId = n2id.get(start);
        int secondId = n2id.get(end);
        access[firstId][secondId]--;
        access[secondId][firstId]--;
        if (Main.DEBUG) {
            assert access[firstId][secondId] >= 0;
            assert access[secondId][firstId] >= 0;
        }
    }
    
    public boolean removeIfIsolated(int node) {
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
    
    public int bfs(int start, int end) throws NodeNotExistException {
        Integer startId = n2id.get(start);
        Integer endId = n2id.get(end);
        if (startId == null) {
            throw new NodeNotExistException(start);
        } if (endId == null) {
            throw new NodeNotExistException(end);
        }
        assert !Main.DEBUG
                || !startId.equals(endId) || distance[startId][endId] == 0;
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
