package model;

import com.oocourse.specs3.models.NodeIdNotFoundException;
import com.oocourse.specs3.models.NodeNotConnectedException;
import com.oocourse.specs3.models.Path;
import com.oocourse.specs3.models.PathNotFoundException;
import com.oocourse.specs3.models.PathIdNotFoundException;
import com.oocourse.specs3.models.RailwaySystem;
import datastructure.Graph;
import datastructure.exceptions.NodeNotExistException;

import java.util.ArrayList;
import java.util.HashMap;

public class MyRailwaySystem extends MyPathContainer implements RailwaySystem {
    private final class Node {
        private final int nodeId;
        private final int pathId;
        
        Node(int nodeId, int pathId) {
            this.nodeId = nodeId;
            this.pathId = pathId;
        }
        
        @Override
        public int hashCode() {
            return nodeId;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Node)) {
                return false;
            }
            Node node = (Node) obj;
            return this.nodeId == node.nodeId && this.pathId == node.pathId;
        }
        
        @Override
        public String toString() {
            return "node" + nodeId + " at line" + pathId;
        }
    }
    
    private Graph<Node> ticketPrice;
    private Graph<Node> unsatisfaction;
    private Graph<Integer> network; // no weight
    private Graph<Integer> transfer; // no weight
    private HashMap<Integer, ArrayList<Integer>> n2lines;
    
    public MyRailwaySystem() {
        ticketPrice = new Graph<>(4000); // 50 * 80
        unsatisfaction = new Graph<>(4000); // 50 * 80
        network = new Graph<>(120); // max distinct node count
        transfer = new Graph<>(50); // max network
        n2lines = new HashMap<>();
    }
    
    private int unpleasant(int firstNode, int secondNode) {
        return (int) Math.pow(4,
                Math.max((firstNode % 5 + 5) % 5, (secondNode % 5 + 5) % 5));
    }
    
    @Override
    public int addPath(Path path) {
        try {
            return super.getPathId(path);
        } catch (PathNotFoundException e) { /* nothing */ }
        int pathId = super.addPath(path);
        if (pathId == 0) {
            return pathId;
        }
        for (int nodeId : path) {
            if (n2lines.get(nodeId) == null) {
                n2lines.put(nodeId, new ArrayList<>());
            } else {
                for (int line : n2lines.get(nodeId)) {
                    ticketPrice.addEdge(new Node(nodeId, pathId),
                            new Node(nodeId, line), 2);
                    unsatisfaction.addEdge(new Node(nodeId, pathId),
                            new Node(nodeId, line), 32);
                    transfer.addEdge(pathId, line, 1);
                }
            }
            n2lines.get(nodeId).add(pathId);
        }
        int firstNode = 0;
        int secondNode = path.getNode(0);
        for (int i = 1; i < path.size(); i++) {
            firstNode = secondNode;
            secondNode = path.getNode(i);
            ticketPrice.addEdge(new Node(firstNode, pathId),
                    new Node(secondNode, pathId), 1);
            unsatisfaction.addEdge(new Node(firstNode, pathId),
                    new Node(secondNode, pathId),
                    unpleasant(firstNode, secondNode));
            network.addEdge(firstNode, secondNode, 1);
        }
        return pathId;
    }
    
    @Override
    public int removePath(Path path) throws PathNotFoundException {
        final int pathId = super.removePath(path);
        for (int nodeId : path) {
            if (n2lines.get(nodeId).size() == 1) {
                n2lines.remove(nodeId);
            } else {
                n2lines.get(nodeId).remove((Integer) pathId);
                for (int line : n2lines.get(nodeId)) {
                    ticketPrice.removeEdge(new Node(nodeId, pathId),
                            new Node(nodeId, line), 2);
                    unsatisfaction.removeEdge(new Node(nodeId, pathId),
                            new Node(nodeId, line), 32);
                    transfer.removeEdge(pathId, line, 1);
                }
            }
        }
        int firstNode;
        int secondNode = path.getNode(0);
        for (int i = 1; i < path.size(); i++) {
            firstNode = secondNode;
            secondNode = path.getNode(i);
            ticketPrice.removeEdge(new Node(firstNode, pathId),
                    new Node(secondNode, pathId), 1);
            unsatisfaction.removeEdge(new Node(firstNode, pathId),
                    new Node(secondNode, pathId),
                    unpleasant(firstNode, secondNode));
            network.removeEdge(firstNode, secondNode, 1);
        }
        return pathId;
    }
    
    @Override
    public void removePathById(int pathId) throws PathIdNotFoundException {
        Path temp = getPathById(pathId);
        try {
            removePath(temp);
        } catch (PathNotFoundException e) {
            if (Main.DEBUG) {
                e.printStackTrace();
            } else {
                throw new PathIdNotFoundException(pathId);
            }
        }
    }
    
    @Override
    public int getDistinctNodeCount() {
        return network.size();
    }
    
    @Override
    public boolean containsNode(int nodeId) {
        return n2lines.get(nodeId) != null;
    }
    
    @Override
    public boolean containsEdge(int fromNodeId, int toNodeId) {
        return network.containsEdge(fromNodeId, toNodeId, 1);
    }
    
    @Override
    public boolean isConnected(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException {
        try {
            return network.bfs(node -> node.equals(fromNodeId),
                node -> node.equals(toNodeId)) != Integer.MAX_VALUE;
        } catch (NodeNotExistException e) {
            if (!network.containsNode(fromNodeId)) {
                throw new NodeIdNotFoundException(fromNodeId);
            } else {
                throw new NodeIdNotFoundException(toNodeId);
            }
        }
    }
    
    @Override
    public int getShortestPathLength(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException, NodeNotConnectedException {
        int result;
        try {
            result = network.bfs(node -> node.equals(fromNodeId),
                node -> node.equals(toNodeId));
        } catch (NodeNotExistException e) {
            if (!network.containsNode(fromNodeId)) {
                throw new NodeIdNotFoundException(fromNodeId);
            } else {
                throw new NodeIdNotFoundException(toNodeId);
            }
        }
        if (result == Integer.MAX_VALUE) {
            throw new NodeNotConnectedException(fromNodeId, toNodeId);
        }
        return result;
    }
    
    @Override
    public int getLeastTicketPrice(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException, NodeNotConnectedException {
        int result;
        try {
            result = ticketPrice
                    .dijskstra(node -> node.hashCode() == fromNodeId,
                        node -> node.hashCode() == toNodeId);
        } catch (NodeNotExistException e) {
            if (!ticketPrice
                    .containsNode(node -> node.hashCode() == fromNodeId)) {
                throw new NodeIdNotFoundException(fromNodeId);
            } else {
                throw new NodeIdNotFoundException(toNodeId);
            }
        }
        if (result == Integer.MAX_VALUE) {
            throw new NodeNotConnectedException(fromNodeId, toNodeId);
        }
        return result;
    }
    
    @Override
    public int getLeastTransferCount(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException, NodeNotConnectedException {
        ArrayList<Integer> fromLines = n2lines.get(fromNodeId);
        ArrayList<Integer> toLines = n2lines.get(toNodeId);
        if (fromLines == null) {
            throw new NodeIdNotFoundException(fromNodeId);
        }
        if (toLines == null) {
            throw new NodeIdNotFoundException(toNodeId);
        }
        for (int line : fromLines) {
            if (toLines.contains(line)) {
                return 0;
            }
        }
        int result;
        try {
            result = transfer
                    .dijskstra(node -> fromLines.contains(node.hashCode()),
                        node -> toLines.contains(node.hashCode()));
        } catch (NodeNotExistException e) {
            throw new NodeNotConnectedException(fromNodeId, toNodeId);
        }
        if (result == Integer.MAX_VALUE) {
            throw new NodeNotConnectedException(fromNodeId, toNodeId);
        }
        return result;
    }
    
    @Override
    public int getLeastUnpleasantValue(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException, NodeNotConnectedException {
        int result;
        try {
            result = unsatisfaction
                    .dijskstra(node -> node.hashCode() == fromNodeId,
                        node -> node.hashCode() == toNodeId);
        } catch (NodeNotExistException e) {
            if (!ticketPrice
                    .containsNode(node -> node.hashCode() == fromNodeId)) {
                throw new NodeIdNotFoundException(fromNodeId);
            } else {
                throw new NodeIdNotFoundException(toNodeId);
            }
        }
        if (result == Integer.MAX_VALUE) {
            throw new NodeNotConnectedException(fromNodeId, toNodeId);
        }
        return result;
    }
    
    @Override
    public int getConnectedBlockCount() {
        return network.connectedBlocks();
    }
    
    @Override
    public int getUnpleasantValue(Path path, int fromIndex, int toIndex) {
        return 0;
    }
}
