package datastructure.exceptions;

import datastructure.Node;

public class NodeNotExistException extends Exception {
    private Node<?> node;
    
    public NodeNotExistException(Node<?> node) {
        this.node = node;
    }
    
    public Node<?> getNode() {
        return node;
    }
}
