package model;

import com.oocourse.specs2.models.Graph;
import com.oocourse.specs2.models.Path;
import com.oocourse.specs2.models.PathIdNotFoundException;
import com.oocourse.specs2.models.PathNotFoundException;
import com.oocourse.specs2.models.NodeIdNotFoundException;
import com.oocourse.specs2.models.NodeNotConnectedException;
import datastructure.exceptions.NodeNotExistException;

public class MyGraph extends MyPathContainer implements Graph {
    private datastructure.Graph graph = new datastructure.Graph();
    
    public MyGraph() {}
    
    @Override
    public int addPath(Path path) {
        int result = super.addPath(path);
        if (result == 0) {
            return result;
        }
        for (int i = 0; i < path.size() - 1; i++) {
            graph.addEdge(path.getNode(i), path.getNode(i + 1));
        }
        graph.initDistance();
        return result;
    }
    
    @Override
    public int removePath(Path path) throws PathNotFoundException {
        final int result = super.removePath(path);
        int firstNode;
        int secondNode = path.getNode(0);
        for (int i = 1; i < path.size(); i++) {
            firstNode = secondNode;
            secondNode = path.getNode(i);
            graph.removeEdge(firstNode, secondNode);
            graph.removeIfIsolated(firstNode);
        }
        graph.removeIfIsolated(secondNode);
        graph.initDistance();
        return result;
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
        return graph.size();
    }
    
    @Override
    public boolean containsNode(int nodeId) {
        return graph.containsNode(nodeId);
    }
    
    @Override
    public boolean containsEdge(int fromNodeId, int toNodeId) {
        return graph.containsEdge(fromNodeId, toNodeId);
    }
    
    @Override
    public boolean isConnected(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException {
        try {
            return graph.bfs(fromNodeId, toNodeId) != Integer.MAX_VALUE;
        } catch (NodeNotExistException e) {
            throw new NodeIdNotFoundException(e.getNodeId());
        }
    }
    
    @Override
    public int getShortestPathLength(int fromNodeId, int toNodeId)
        throws NodeIdNotFoundException, NodeNotConnectedException {
        int result;
        try {
            result = graph.bfs(fromNodeId, toNodeId);
        } catch (NodeNotExistException e) {
            throw new NodeIdNotFoundException(e.getNodeId());
        }
        if (result == Integer.MAX_VALUE) {
            throw new NodeNotConnectedException(fromNodeId, toNodeId);
        }
        return result;
        
    }
}
