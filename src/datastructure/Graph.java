package datastructure;

import datastructure.exceptions.NodeNotExistException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * precondition:
 *     distinct nodes cannot exceed capacity;
 *     no isolate node exists;
 *         (guaranteed by contract valid path consists of at least 2 nodes)
 */
public class Graph<T> {
    private final int capacity;
    
    private final class Access {
        // firstId -> secondId -> weights
        // firstId <= secondId
        private boolean[][] data;
        private HashMap<Integer, ArrayList<Integer>> base;
        
        Access() {
            data = new boolean[capacity][capacity];
            for (int i = 0; i < capacity; i++) {
                for (int j = 0; j < capacity; j++) {
                    data[i][j] = false;
                }
            }
            base = new HashMap<>();
        }
        
        private int hash(int a, int b) {
            if (a <= b) {
                return a * capacity + b;
            } else {
                return b * capacity + a;
            }
        }
        
        boolean add(int firstId, int secondId, int e) {
            int pos = hash(firstId, secondId);
            if (!data[firstId][secondId]) {
                data[firstId][secondId] = true;
                data[secondId][firstId] = true;
                base.put(pos, new ArrayList<>());
            }
            return base.get(pos).add(e);
        }
        
        boolean contains(int firstId, int secondId, int e) {
            if (data[firstId][secondId]) {
                return base.get(hash(firstId, secondId)).contains(e);
            } else {
                return false;
            }
        }
        
        boolean remove(int firstId, int secondId, Integer e) {
            int pos = hash(firstId, secondId);
            boolean result = base.get(pos).remove(e);
            if (result && base.get(pos).size() == 0) {
                base.remove(pos);
                data[firstId][secondId] = false;
                data[secondId][firstId] = false;
            }
            return result;
        }
        
        boolean areNeighbours(int firstId, int secondId) {
            return firstId != secondId && data[firstId][secondId];
        }
        
        int min(int firstId, int secondId) {
            return Collections.min(base.get(hash(firstId, secondId)));
        }
    }
    
    private HashMap<T, Integer> n2id;
    private LinkedList<Integer> vacantIds = new LinkedList<>();
    private Access access; // entries non-neg
    
    public Graph(int capacity) {
        this.capacity = capacity;
        n2id = new HashMap<>(capacity);
        access = new Access();
        for (int i = 0; i < capacity; i++) {
            vacantIds.add(i);
        }
    }
    
    public int size() {
        return n2id.size();
    }
    
    private int getNodeId(T node) {
        Integer result = n2id.get(node);
        if (result == null) {
            result = vacantIds.removeFirst();
            n2id.put(node, result);
        }
        return result;
    }
    
    public boolean containsNode(T node) {
        return n2id.get(node) != null;
    }
    
    public boolean containsNode(Predicate<T> predicate) {
        try {
            filterNodes(predicate);
            return true;
        } catch (NodeNotExistException e) {
            return false;
        }
    }
    
    public boolean containsEdge(T start, T end, int weight) {
        Integer firstId = n2id.get(start);
        Integer secondId = n2id.get(end);
        if (firstId == null || secondId == null) {
            return false;
        }
        return access.contains(firstId, secondId, weight);
    }
    
    public void addEdge(T start, T end, int weight) {
        access.add(getNodeId(start), getNodeId(end), weight);
    }
    
    private void removeIfIsolated(T node) {
        Integer id = n2id.get(node);
        if (id == null) {
            return;
        }
        for (int i = 0; i < capacity; i++) {
            if (access.data[id][i]) {
                return;
            }
        }
        n2id.remove(node);
        vacantIds.addFirst(id);
    }
    
    public void removeEdge(T start, T end, int weight) {
        access.remove(n2id.get(start), n2id.get(end), weight);
        removeIfIsolated(start);
        removeIfIsolated(end);
    }
    
    private Set<Integer> filterNodes(Predicate<T> predicate)
            throws NodeNotExistException {
        Set<Integer> result = n2id.entrySet().stream()
                .filter(entry -> predicate.test(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
        if (result.size() == 0) {
            throw new NodeNotExistException(predicate);
        }
        return result;
    }
    
    public int bfs(Predicate<T> start, Predicate<T> end)
            throws NodeNotExistException {
        Set<Integer> startId = filterNodes(start);
        Set<Integer> endId = filterNodes(end);
        for (int id : startId) {
            if (endId.contains(id)) {
                return 0;
            }
        }
        HashSet<Integer> visited = new HashSet<>(startId);
        HashSet<Integer> currentlyVisiting = new HashSet<>(startId);
        HashSet<Integer> toVisit = new HashSet<>();
        for (int length = 1; true; length++) {
            if (currentlyVisiting.size() == 0) {
                return Integer.MAX_VALUE;
            }
            for (int id : currentlyVisiting) {
                for (int validId : n2id.values()) {
                    if (!access.areNeighbours(id, validId)
                            || visited.contains(validId)) { // already visited
                        continue;
                    }
                    if (endId.contains(validId)) {
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
    
    public int connectedBlocks() {
        ArrayList<Integer> remaining = new ArrayList<>(n2id.values());
        HashSet<Integer> visited = new HashSet<>();
        ArrayList<Integer> currentlyVisiting = new ArrayList<>();
        int count = 0;
        while (!remaining.isEmpty()) {
            if (currentlyVisiting.isEmpty()) {
                count++;
                currentlyVisiting.add(remaining.remove(0));
            }
            int id = currentlyVisiting.remove(0);
            visited.add(id);
            for (int i = 0; i < capacity; i++) {
                if (access.areNeighbours(id, i)
                        && !visited.contains(i)
                        && !currentlyVisiting.contains(i)) {
                    currentlyVisiting.add(i);
                    remaining.remove((Integer) i);
                }
            }
        }
        return count;
    }
    
    public int dijskstra(Predicate<T> start, Predicate<T> end)
            throws NodeNotExistException {
        Set<Integer> candidates = filterNodes(start);
        Set<Integer> endId = filterNodes(end);
        for (int id : candidates) {
            if (endId.contains(id)) {
                return 0;
            }
        }
        int[] distance = new int[capacity];
        for (int i = 0; i < capacity; i++) {
            distance[i] = Integer.MAX_VALUE;
        }
        for (int id : candidates) {
            distance[id] = 0;
        }
        while (candidates.size() != 0) {
            int id = -1;
            int value = Integer.MAX_VALUE;
            for (int candidate : candidates) {
                if (distance[candidate] < value) {
                    value = distance[candidate];
                    id = candidate;
                }
            }
            if (endId.contains(id)) {
                return value;
            }
            candidates.remove(id);
            for (int validId : n2id.values()) {
                if (!access.areNeighbours(id, validId)) {
                    continue;
                }
                if (distance[validId] == Integer.MAX_VALUE) {
                    candidates.add(validId);
                    distance[validId] = value + access.min(id, validId);
                } else {
                    distance[validId] = Math.min(distance[validId],
                            value + access.min(id, validId));
                }
            }
        }
        return Integer.MAX_VALUE;
    }
}
