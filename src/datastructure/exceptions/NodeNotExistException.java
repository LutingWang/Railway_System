package datastructure.exceptions;

public class NodeNotExistException extends Exception {
    private Object node;
    
    public NodeNotExistException(Object node) {
        this.node = node;
    }
    
    public Object getNode() {
        return node;
    }
}
