package datastructure;

import com.sun.istack.internal.NotNull;

import java.util.HashMap;

public class Node<T> {
    private final T nodeId;
    public final HashMap<Node<T>, Integer> adj = new HashMap<>();
    
    public Node(@NotNull T nodeId) {
        this.nodeId = nodeId;
    }
    
    public T getNodeId() {
        return nodeId;
    }
    
    public HashMap<Node<T>, Integer> getAdj() {
        return adj;
    }
    
    public void neighbour(Node<T> node, int weight) {
        adj.put(node, weight);
    }
    
    @Override
    public int hashCode() {
        return nodeId.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node)) {
            return false;
        }
        Node node = (Node) obj;
        return nodeId.equals(node.nodeId);
    }
}
