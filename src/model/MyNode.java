package model;

import datastructure.Node;

public class MyNode extends Node<Integer> {
    private int pathId;
    
    public MyNode(int id, int nodeId, int pathId) {
        super(id, nodeId);
        this.pathId = pathId;
    }
    
    public boolean samePath(MyNode myNode) {
        return this.pathId == myNode.pathId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MyNode)) {
            return false;
        }
        MyNode myNode = (MyNode) obj;
        return this.pathId == myNode.pathId && super.equals(myNode);
    }
}
