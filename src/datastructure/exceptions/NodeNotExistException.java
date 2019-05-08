package datastructure.exceptions;

public class NodeNotExistException extends Exception {
    private int nodeId;
    
    public NodeNotExistException(int nodeId) {
        this.nodeId = nodeId;
    }
    
    public int getNodeId() {
        return nodeId;
    }
}
